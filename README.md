# expense-splitter API

API do wieloplatformowej aplikacji, której zadaniem jest dzielenie, zarządzanie i kontrolowanie wydatków w grupie.

## Uruchomienie

Aby uruchomić aplikację, w celach programistcznych, najlepiej skorzystać ze skonfigurowanego w projekcie Dockera.

W tym celu należy wykonać następujące kroki:

---

### Linux

1. Przejdź do katalogu projektu w terminalu.
2. Jeśli to pierwsze uruchomienie, wykonaj polecenie `cp .env.example .env` w celu utworzenia pliku konfiguracyjnego.
3. Uruchom swój ulubiony edytor tekstowy i otwórz plik `.env`, dostosuj ustawienia, które tego wymagają (np. dane do bazy danych).
4. W terminalu, będąc w katalogu projektu, wykonaj polecenie `docker-compose up --build -d` w celu zbudowania i uruchomienia kontenerów Dockera.

---

### Windows
1. Otwórz PowerShell i przejdź do katalogu projektu.
2. Jeśli to pierwsze uruchomienie, wykonaj polecenie `Copy-Item .env.example -Destination .env` w celu utworzenia pliku konfiguracyjnego.
3. Otwórz plik `.env` w edytorze tekstowym i dostosuj ustawienia, które tego wymagają (np. dane do bazy danych).
4. W PowerShell, będąc w katalogu projektu, wykonaj polecenie `docker-compose up --build -d` w celu zbudowania i uruchomienia kontenerów Dockera.

---

Po zakończeniu procesu, API będzie dostępne pod adresem `http://localhost:8080`.

Logi aplikacji można podejrzeć za pomocą polecenia `docker-compose logs -f` w terminalu.

Po uruchomieniu API, jego stan można obserować pod adresem url: `http://localhost:8080/api/actuator/health`.

## Autor
[Sebastian Górski](https://github.com/sgorski00)