# Map Project

### Technologies

* Play Framework 2.2.3
* AngularJS
* Leaflet.js (genom angular-leaflet-directive)

### API's
**MediaWiki:**

För att hämta content från wikipediaartiklar

**Wikilocation:**

Hämtade en databasdump därfrån. Det ligger en databasdump if conf/default-db som man kan använda för att fylla sin databas.
Skapa din egen application.conf i conf/ som inkluderar base-application.conf ('include "base-application.conf"' överst)

### Installationer
    $ brew install play

Eller ladda ned activator eller en vanlig zip från [http://www.playframework.com/download]()

    $ cd <PROJECT_ROOT>
    $ npm install
    $ bower install

Starta dev servern med ``play ~run`` från projektroten

Testerna kan köras med ``play test``

Starta grunt med ``grunt``

Javascripttesterna kan köras med ``karma start`` (för att starta servern) i en terminal följt av ``karma run``
i en annan för att köra testerna.