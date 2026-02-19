# Flight Search Service

Birden fazla ucus saglayicisini (SOAP) tuketip, birlestirerek REST API uzerinden sunan bir ucus arama servisi.

## Mimari

```
                                 +---------------------+
                                 |   FlightProviderA   |
                          SOAP   |    (port 8081)      |
                       +-------->|  WSDL: /ws/flights  |
                       |         +---------------------+
+----------+     +-----------+
|  Client  |---->| CaseProject|
| (REST)   |     | (port 8080)|
+----------+     +-----------+
                       |         +---------------------+
                       |  SOAP   |   FlightProviderB   |
                       +-------->|    (port 8082)      |
                                 |  WSDL: /ws/flights  |
                                 +---------------------+
                       |
                       v
                 +------------+
                 | PostgreSQL |
                 | (Log)      |
                 +------------+
```

## Teknoloji Stack

| Teknoloji | Versiyon |
|-----------|----------|
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Web Services | SOAP server + client |
| JAX-WS (wsimport) | 4.0.3 |
| JAXB | Jakarta XML Bind 4.0 |
| Spring Data JPA | Hibernate 6.4.4 |
| PostgreSQL | 42.6.2 (driver) |
| Jackson JSR310 | Java 8 Date/Time |
| Maven | 3.9.12 |

## Projeler

### 1. FlightProviderA (port 8081)

SOAP web servisi. Ucus arama islemi yapar ve sonuclari XML olarak doner.

- **WSDL:** `http://localhost:8081/ws/flights.wsdl`
- **Namespace:** `http://flightprovidera.com/soap`
- **Alan adlari:** `flightNo`, `origin`, `destination`

```
FlightProviderA/
  pom.xml
  src/main/java/com/flightprovidera/
    FlightProviderAApplication.java
    config/WebServiceConfig.java
    endpoint/FlightSearchEndpoint.java
    service/
      Flight.java
      SearchRequest.java
      SearchResult.java
      SearchService.java
    util/DateTimeConverter.java
  src/main/resources/
    application.properties
    xsd/flight-provider-a.xsd
```

### 2. FlightProviderB (port 8082)

SOAP web servisi. ProviderA ile ayni islevi yapar, farkli alan adlari kullanir.

- **WSDL:** `http://localhost:8082/ws/flights.wsdl`
- **Namespace:** `http://flightproviderb.com/soap`
- **Alan adlari:** `flightNumber`, `departure`, `arrival`

```
FlightProviderB/
  pom.xml
  src/main/java/com/flightproviderb/
    FlightProviderBApplication.java
    config/WebServiceConfig.java
    endpoint/FlightSearchEndpoint.java
    service/
      Flight.java
      SearchRequest.java
      SearchResult.java
      SearchService.java
    util/DateTimeConverter.java
  src/main/resources/
    application.properties
    xsd/flight-provider-b.xsd
```

### 3. CaseProject (port 8080)

REST aggregator servisi. Her iki SOAP provider'i tuketir, ucuslari birlestirir ve REST API olarak sunar. Tum istek/yanit bilgilerini PostgreSQL'e loglar.

```
CaseProject/
  pom.xml
  src/main/java/com/casestudy/
    CaseApplication.java
    config/SoapClientConfig.java
    client/
      providera/ProviderASoapClient.java
      providerb/ProviderBSoapClient.java
    controller/FlightSearchController.java
    dto/
      FlightSearchRequest.java
      FlightDto.java
      FlightSearchResponse.java
      FlightGroupKey.java
    mapper/FlightMapper.java
    service/FlightAggregatorService.java
    entity/ServiceLog.java
    repository/ServiceLogRepository.java
    filter/LoggingFilter.java
    util/DateTimeConverter.java
  src/main/resources/
    application.yml
    wsdl/
      provider-a.wsdl
      provider-b.wsdl
  src/test/java/com/casestudy/controller/
    FlightSearchControllerTest.java
```

**WSDL Stub Uretimi:** `jaxws-maven-plugin` (wsimport) ile WSDL dosyalarindan JAXB stub siniflari otomatik uretilir:
- `provider-a.wsdl` -> `com.casestudy.client.providera.gen` paketi
- `provider-b.wsdl` -> `com.casestudy.client.providerb.gen` paketi

**Alan Normalizasyonu (FlightMapper):**

| Provider A | Provider B | Unified DTO |
|------------|------------|-------------|
| flightNo | flightNumber | flightNumber |
| origin | departure | origin |
| destination | arrival | destination |

## REST API

### Endpoint 1: Tum Ucuslari Ara

Her iki provider'dan gelen tum ucuslari birlestirir.

```
POST http://localhost:8080/api/flights/search
Content-Type: application/json

{
    "origin": "IST",
    "destination": "COV",
    "departureDate": "2026-03-15T00:00:00"
}
```

**Yanit:** 18 ucus (9 ProviderA + 9 ProviderB)

```json
{
    "flights": [
        {
            "flightNumber": "TK1001",
            "origin": "IST",
            "destination": "COV",
            "departureDateTime": "2026-03-15T09:00:00",
            "arrivalDateTime": "2026-03-15T13:51:00",
            "price": 159.0
        }
    ]
}
```

### Endpoint 2: En Ucuz Ucuslari Ara

Tum ucuslari birlestirir, 5 alana gore gruplar ve her gruptaki en ucuz ucusu secer.

```
POST http://localhost:8080/api/flights/search/cheapest
Content-Type: application/json

{
    "origin": "IST",
    "destination": "COV",
    "departureDate": "2026-03-15T00:00:00"
}
```

**Gruplama alanlari:** `flightNumber`, `origin`, `destination`, `departureDateTime`, `arrivalDateTime`

Her gruptan `price` degeri en dusuk olan ucus secilir.

## Veritabani Loglama

Tum `/api/*` istekleri otomatik olarak PostgreSQL `service_log` tablosuna kaydedilir:

| Alan | Tip | Aciklama |
|------|-----|----------|
| id | BIGSERIAL | Primary key |
| timestamp | TIMESTAMP | Istek zamani |
| service_name | VARCHAR(100) | Servis adi |
| endpoint_path | VARCHAR(255) | Endpoint yolu |
| http_method | VARCHAR(10) | HTTP metodu |
| request_body | TEXT | Istek govdesi (JSON) |
| response_body | TEXT | Yanit govdesi (JSON) |
| http_status | INTEGER | HTTP durum kodu |
| duration_ms | BIGINT | Sure (milisaniye) |

Tablo `Hibernate ddl-auto=update` ile otomatik olusturulur.

## Kurulum ve Calistirma

### Gereksinimler

- Java 17+
- Maven 3.9+
- PostgreSQL veritabani

### 1. Veritabani Yapilandirmasi

Proje root dizininde `.env` dosyasi olusturun:

```
DB_URL=jdbc:postgresql://localhost:5432/flightcase
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

### 2. FlightProviderA'yi Baslat

```bash
cd FlightProviderA
mvn clean spring-boot:run
```

Dogrulama: `http://localhost:8081/ws/flights.wsdl`

### 3. FlightProviderB'yi Baslat

```bash
cd FlightProviderB
mvn clean spring-boot:run
```

Dogrulama: `http://localhost:8082/ws/flights.wsdl`

### 4. CaseProject'i Baslat

Linux/Mac:
```bash
export $(cat .env | xargs) && cd CaseProject && mvn clean spring-boot:run
```

Windows (PowerShell):
```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
cd CaseProject
mvn clean spring-boot:run
```

### 5. Test

```bash
# Tum ucuslar
curl -X POST http://localhost:8080/api/flights/search \
  -H "Content-Type: application/json" \
  -d '{"origin":"IST","destination":"COV","departureDate":"2026-03-15T00:00:00"}'

# En ucuz ucuslar
curl -X POST http://localhost:8080/api/flights/search/cheapest \
  -H "Content-Type: application/json" \
  -d '{"origin":"IST","destination":"COV","departureDate":"2026-03-15T00:00:00"}'
```

## Unit Testler

CaseProject icin controller unit testleri mevcuttur:

```bash
cd CaseProject
mvn test
```

7 test bulunur (Mockito ile):
- `searchAllFlights_returnsFlightList` - Basarili cagri, 3 ucus doner
- `searchAllFlights_emptyResult` - Bos sonuc kontrolu
- `searchAllFlights_passesCorrectParameters` - Parametre iletimi dogrulamasi
- `searchAllFlights_dateTimeFieldsPreserved` - Tarih alanlari korunur
- `searchCheapestFlights_returnsGroupedCheapest` - Gruplanmis en ucuz ucuslar
- `searchCheapestFlights_emptyResult` - Bos sonuc kontrolu
- `searchCheapestFlights_singleGroup` - Tek grup kontrolu

## Port Ozeti

| Servis | Port | Protokol |
|--------|------|----------|
| FlightProviderA | 8081 | SOAP |
| FlightProviderB | 8082 | SOAP |
| CaseProject | 8080 | REST |
