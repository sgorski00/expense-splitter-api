# expense-splitter API

API do wieloplatformowej aplikacji, której zadaniem jest dzielenie, zarządzanie i kontrolowanie wydatków w grupie.

## Dokumentacja API

Dokumentacja API jest udostępniona jako strona statyczna w [GitHub Pages](https://sgorski00.github.io/expense-splitter-api/).

Można ją również sprawdzić lokalnie z interfejsem Swagger UI po uruchomieniu aplikacji. W tym celu należy odwiedzić adres `http://localhost:8080/api/swagger-ui/index.html` w przeglądarce internetowej.

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

---

## Uwierzytelnianie w aplikacji

API udostępnia 2 metody uwierzytelniania - lokalnie oraz OAuth2.

Uwierzytelnienie odbywa się na podstawie tokena JWT oraz Refresh Tokena, które są generowane po poprawnym zalogowaniu się do systemu.

Po poprawnym uwierzytelnieniu zarówno token JWT (access token), jak i Refresh Token będą zwracane w ciele odpowiedzi.
Refresh Token będzie również ustawiany jako `httpOnly cookie` o nazwie `refreshToken`.
**Jest to zalecany sposób przechowywania Refresh Tokena w aplikacji webowej, ponieważ HttpOnly cookies są niedostępne dla JavaScriptu, co zwiększa bezpieczeństwo przed atakami typu XSS.**

### Rejestracja

Użytkownik może zarejestrować się w systemie, wykonując żądanie POST na endpoint `/api/auth/register` z danymi rejestracyjnymi w formacie JSON. Po poprawnej rejestracji, użytkownik będzie mógł się zalogować i korzystać z funkcjonalności API.

Rejestracja może również odbywć się za pomocą protokołu OAuth2, o ile wcześniej nie istniał użytkownik o podanym u dostawcy adresie email.

### Logowanie Lokalnie

Aby uwierzytelnić się lokalnie, należy wykonać następujące kroki:
1. Wykonaj żądanie POST na endpoint `/api/auth/login` z danymi logowania w formacie JSON.
2. Po poprawnym zalogowaniu się, tokeny będą wydane zgodnie z zasadami opisanymi wcześniej.

### Logowanie OAuth2

Aby uwierzytelnić się za pomocą OAuth2, należy wykonać następujące kroki:
1. Wykonaj żądanie POST na endpoint `/api/oauth2/authorization/{provider}`, gdzie `{provider}` to nazwa dostawcy OAuth2 (np. `google`, `facebook`).
2. Nastąpi przekierowanie do strony logowania dostawcy OAuth2, gdzie należy się zalogować i udzielić zgody na dostęp do danych.
3. Po poprawnym zalogowaniu się, tokeny będą wydane zgodnie z zasadami opisanymi wcześniej.

### Odświeżanie tokenów

Tokeny JWT mają określony czas ważności, po którym wygasają. 

Aby odświeżyć token JWT, należy wykonać żądanie POST na endpoint `/api/auth/refresh` z Refresh Tokenem w HttpOnly cookie o nazwie `refreshToken`. 
Alternatywnym sposobem jest przesłanie Refresh Tokena w wymienionym wyżej endpoincie bez użycia cookie. W tym przypadku refresh token należy przesłać w nagłówku `Authorization` w formacie `Bearer {refreshToken}`.
Po poprawnym odświeżeniu, otrzymasz nowy token JWT oraz Refresh Token.

### Wylogowanie

Aby wylogować się z systemu, należy wykonać żądanie POST na endpoint `/api/auth/logout`. 

Spowoduje to unieważnienie Refresh Tokena oraz usunięcie HttpOnly cookie `refreshToken` oraz `accessToken`.

### Dodatkowe informacje
- Przy zmianie/ustawieniu hasła, wszystkie istniejące Refresh Tokeny dla danego użytkownika zostaną unieważnione, co wymusi ponowne logowanie się użytkownika.
- W przypadku wykorzystania refresh tokena w enpoincie `/api/auth/refresh`, stary refresh token zostanie unieważniony, a nowy będzie ważny do czasu jego wygaśnięcia lub ręcznego unieważnienia (np. poprzez wylogowanie).
- Refresh Tokeny są cyklicznie usuwane z bazy, jeżeli są przeterminowane lub zużyte.

---

## Autor
[Sebastian Górski](https://github.com/sgorski00)