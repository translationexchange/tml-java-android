<p align="center">
  <img src="https://avatars0.githubusercontent.com/u/1316274?v=3&s=200">
</p>

TML for Android
===

TML for Android provides extensions for translating Android apps.

Installation
==================

If you are using Maven:

Add the following dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.translationexchange</groupId>
  <artifactId>tml-android</artifactId>
  <version>0.2.1</version>
</dependency>
```

If you use Eclipse or IntelliJ IDEs, the dependencies will be automatically installed in your project. 


Integration
==================

You now can initialize the SDK using the following code:

```java
Tml.init(YOUR_APP_TOKEN);
```

TML (Translation Markup Language)
==================

You now can use the translation methods provided by TML. Below are some examples:

Simple labels:

```java
Tml.translate("Hello World");
```


Labels with dynamic data:

```java
Tml.translate(
  "You have selected {language_name} language", 
  Utils.buildMap(
      "language_name", Tml.getCurrentLanguage().getEnglishName()
  )
);
          
Tml.translate(
  "Number of messages: {count}", 
  Utils.buildMap(
    "count", 5
  )
);

Tml.translate(
  "Hello {user.name}, you are a {user.gender}", 
  Utils.buildMap(
      "user", Utils.buildMap(
                "name", "Michael", 
                "gender", "male"
              )
      )
);

Tml.translate(
  "You have {count||message}", 
  Utils.buildMap(
    "count", 5
  )
);

Tml.translate(
  "{user| He, She} likes this movie.", 
  Utils.buildMap(
    "user", Utils.buildMap(
              "gender", "male"
            )
     )
);
      
Tml.translate(
  "{user} uploaded {count|| photo} to {user| his, her} photo album.", 
  Utils.buildMap(
    "user", Utils.buildMap(
              "object", Utils.buildMap(
                  "name", "Michael", 
                  "gender", "male"
              ),
              "attribute", "name"
        ),
    "count", 1
    )
);

```

Labels with decorations (using HTML):

```java
Tml.translate(
    "[bold: Adjust fonts] using HTML.", 
    Utils.buildMap("bold", "<strong>{$0}</strong>")
);

Tml.translate(
    "[red: Change color] using HTML.", 
    Utils.buildMap(
        "red", "<font color='red'>{$0}</font>"
    )
);

Tml.translate(
  "Nest [bold]some bold and [italic: italic][/bold] using HTML.", 
  Utils.buildMap(
      "italic", "<i>{$0}</i>", 
      "bold", "<strong>{$0}</strong>"
  )
);

```

Labels with data and decoration tokens (using SpannableString):

```java
Tml.translateSpannableString(
  "[link: {actor}] uploaded [bold: {count|a document, #count# documents}] to a public folder.", 
  Utils.buildMap(
        "actor", user,
        "count", 10,
        "link", Utils.buildMap("color", "blue"),
        "bold", Utils.buildMap("style", "bold")
    ));
    
Tml.translateSpannableString(
  "[link: {actor}] tagged [link: {target}] in [link: {owner::pos}] photo.", 
  Utils.buildMap(
        "actor", user1,
        "target", user2,
        "owner", user3,
        "link", Utils.buildMap("color", "blue")
    ));
```


Sample Applications
==================

The best way to get started with Tml is to see it in action. A number of sample applications are available for you to see how the SDK can be integrated and used:

* Tml Android Samples: [tml-android-samples](https://github.com/translationexchange/tml-android-samples)
* Wammer App: [tml-java-android-samples-wammer](https://github.com/translationexchange/tml-java-android-samples-wammer)


Links
==================

* Register on TranslationExchange.com: https://translationexchange.com

* Follow TranslationExchange on Twitter: https://twitter.com/translationx

* Connect with TranslationExchange on Facebook: https://www.facebook.com/translationexchange

* If you have any questions or suggestions, contact us: support@translationexchange.com


Copyright and license
==================

Copyright (c) 2015 Translation Exchange, Inc.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


