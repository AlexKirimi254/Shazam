# ShazamApp
Celem projektu było stworzenie aplikacji moblilnej rozpozającej utwory muzyczne (na wzór słynnego Shazama). Należało stowrzyć połączenie z serwerem bazodanowym na którym przechowywane były stosownie
przetworzone utowry przystosowane do szybkiego rozpoznawania ich. Miało to na celu rozwinięcie umiejętnętności programowanie aplikacji mobilnych (dla środowiska Android Studio).

# Proces rozpoznawanie dźwięku

## Nagrywanie dźwięku

Najpierw opracowano metodę nagrywania dźwięku do rozpoznania (dźwięk który jest w tle i należy
go nagrać, aby można było go w szybki dla komputera sposób, redukucjąc niepotrzebne dane, rozpoznać spośród
bazy utworów wczęśniej sporządzonej). Ludzie ucho jest najbardziej czułe w paśmie częstotliwości akustycznych około [100,4000] kHz, przy czym
maksimum tej czułości jest dla częstotliwości około 3000 kHz. Nagrywano dźwięk trwający 10s, gdyż podczas testów dla tej długości 
nagrania uzyskiwano akceptowalną precyzję wykryć

## Proces przetwarzania dźwięku

Aby utwór można bylo rozpoznać oraz zapisać utwór do postaci takiej, aby rozpoznanie późniejsze tego
utworu było szybsze należy stosowanie przetworzyć nagranie dźwiękowe, aby pozbawić go niepotrzebnych z perspektywy rozpoznawania utowru, zarówno dla człowieka a zatem i komputera informacji. Człowiek zapapmietuje
tylko kluczowe informacje więc taka redukcja jest potrzebna. Poza tym rozpoznanie za pomocą mniejszej ilości
informacji jest szybsze dla komputera. Program wiec wykonywał poniższe operacje:

* Generowanie spektorgramu aby uzyskać wykres częstotliwości w funkcji czasu (za pomocą tzw. transformaty FFT)
* Uzyskiwanie punktów kluczowych aby wydobyć tylko naistotniejsze punkty spektrogramu, które są
* Charakterystyczne dla tego utworu (aby np. pozbyć się wpływu szumu itp.)
* Generowanie na podstawie punktów kluczowych par tzw. hashpoints. (aby uniezależnić ropzoznanie utowru
od momentu rozpoczęcia nagrywania dźwięku w tle).

Procesy te dają finalnie zbiór tzw. <i>hashpoints</i> które są finalna formą prztworzonego utworu, dzięki któemu
możliwe jest rozpoznawanie jego spośród innych.

## Rozpoznawanie utworów na podstawie wcześniej przetworzonych utworów.

Po zatrzymaniu nagrywania przez mikrofon odsłuchiwanego utworu, w celu ograniczenia ilości przesyłanych
danych, wysyłamy jedynie zhashowane punkty na serwer bazodanowy, w celu policzenia, ile punktów pasuje do
naszego utworu.

Następnie, z uwzględnieniem niewielkiego błędu pomiarowego, wybierany jest utwór, dla którego otrzymujemy
najwięcej dopasowań. W przypadku, gdy jest problem z określeniem, który utwór jest utworem odsłuchiwanym (ilość hashy, które
pasują do utworu jest podobna w kilku przypadkach).

Przetworzone utwory przechowywano w relacyjnej bazie danych.

System obsługuje 2 metody rozpoznawania utworów. Pierwszy jest to system działający w Javie, który jest w stanie rozpoznawać zapisane w utowry w trybie offline. Drugim
systemem jest sytem bazo-danowy, który wykorzystuje mechanizmy opisane powyżej. Jest to podłoże do wprowadzenia aplikacji w tryb offline. Na zasadzie zapamiętywania najpopularniejszych utwór, które potem aplkacja
będzie w stanie rozpoznać za pomocą zapisanych Hash
