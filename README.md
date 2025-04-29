# Konfiguracja Krakend API Gateway

## Jak dodać nowy endpoint do Krakenda?

1. **Otwórz plik `krakend.json`**  
   Plik ten znajduje się w głównym katalogu projektu i zawiera konfigurację wszystkich endpointów obsługiwanych przez Krakend.

2. **Dodaj nowy obiekt w sekcji `endpoints`**  
   Każdy endpoint opisuje trasę, metodę HTTP, sposób mapowania parametrów oraz backend, do którego Krakend przekazuje żądania.

   Przykład nowego endpointu:
   ```json
   {
      "endpoint": "/reservations",
      "method": "GET",
      "output_encoding": "json",
      "backend": [
        {          
          "url_pattern": "/api/reservations",
          "host": ["http://rental-room-service:8081"],
          "encoding": "json",
          "method": "GET",
          "is_collection": true
        }
      ]
    }
   ```
   * enpoint - ścieżka widoczna na zewnątrz
   * url_pattern - ścieżka, na którą Krakend przekazuje żądanie do mikroserwisu
   * host - adres serwisu backendowego
   * is_collection - parametr niezbędny w przypadku zwracania kolekcji (domyślnie ustawiony na false)
     
3. **Zrestartuj Krakend**  
   Po każdej zmianie w pliku krakend.json należy zrestartować usługę Krakend, aby nowe endpointy były dostępne.
