<p align="center">
  <img src="https://raw.github.com/tr8n/tr8n/master/doc/screenshots/tr8nlogo.png">
</p>

Tr8n Client SDK for Android.
===

[![Project status](http://stillmaintained.com/tr8n/tr8n_android_clientsdk.png)](http://stillmaintained.com/tr8n/tr8n_android_clientsdk.png)

Tr8n Client SDK for Android provides extensions for building Android apps.

Installation
==================

If you are using Maven:

Add the following dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.tr8nhub</groupId>
  <artifactId>android-clientsdk</artifactId>
  <version>0.1.0</version>
</dependency>
```

If you use Eclipse or IntelliJ IDEs, the dependencies will be automatically downloaded. This SDK depends on Tr8n Core library that will be installed and added to your classpath.


Integration
==================

You now can initialize the SDK using the following code:

```java
Tr8n.getConfig().setApplication(Utils.buildStringMap(
      "key", "YOUR_APP_KEY",
      "secret", "YOUR_APP_SECRET"
      ));
```

Keep in mind that all of the code in Tr8n Core is single threaded. It is up to the container application to ensure that certain methods are called in a separate thread.
For instance, the above Tr8n initialization code will use network, if available, to download the latest language definitions for the current language.
Therefore it is necessary to execute this code in a separate thread. A splash screen is a good way to initialize the application assets - and that would be a good place to put the above code.

Here is an example of how the initialization done in a splash screen:

[SplashScreenActivity.java](https://github.com/tr8n/tr8n_samples_wammer_android/blob/master/src/main/java/com/tr8n/samples/wammer/activities/SplashScreenActivity.java)

TML (Translation Markup Language)
==================

You now can use the translation methods provided by Tr8n. Below are some examples:

Simple labels:

```java
Tr8n.translate("Hello World");
```


Labels with dynamic data:

```java
Tr8n.translate("You have selected {language_name} languge", 
              Utils.buildMap("language_name", Tr8n.getCurrentLanguage().getEnglishName()));
          
Tr8n.translate("Number of messages: {count}", Utils.buildMap("count", 5));

Tr8n.translate("Hello {user.name}, you are a {user.gender}", 
               Utils.buildMap("user", Utils.buildMap("name", "Michael", "gender", "male")));

Tr8n.translate("You have {count||message}", Utils.buildMap("count", 5));

Tr8n.translate("{user| He, She} likes this movie.", Utils.buildMap("user", Utils.buildMap("gender", "male")));
      
Tr8n.translate("{user} uploaded {count|| photo} to {user| his, her} photo album.", 
          Utils.buildMap(
              "user", Utils.buildMap(
                    "object", Utils.buildMap("name", "Michael", "gender", "male"),
                    "attribute", "name"
                  ),
              "count", 1
              ));
```

Labels with decorations (using HTML):

```java
Tr8n.translate("[bold: Adjust fonts] using HTML.", Utils.buildMap("bold", "<strong>{$0}</strong>"));

Tr8n.translate("[red: Change color] using HTML.", Utils.buildMap("red", "<font color='red'>{$0}</font>"));

Tr8n.translate("Nest [bold]some bold and [italic: italic][/bold] using HTML.", Utils.buildMap("italic", "<i>{$0}</i>", "bold", "<strong>{$0}</strong>"));
```

Labels with data and decoration tokens (using SpannableString):

```java
Tr8n.translateSpannableString("[link: {actor}] uploaded [bold: {count|a document, #count# documents}] to a public folder.", Utils.buildMap(
        "actor", getActor(),
        "count", getCount(),
        "link", Utils.buildMap("color", "blue"),
        "bold", Utils.buildMap("style", "bold")
    ));
    
Tr8n.translateSpannableString("[link: {actor}] tagged [link: {target}] in [link: {owner::pos}] photo.", Utils.buildMap(
        "actor", getActor(),
        "target", getTarget(),
        "owner", getOwner(),
        "link", Utils.buildMap("color", "blue")
    ));
```


Sample Applications
==================

The best way to get started with Tr8n is to see it in action. A number of sample applications are available for you to see how the SDK can be integrated and used:

* Tr8n Android Samples: [tr8n_android_samples](https://github.com/tr8n/tr8n_android_samples)
* Wammer App: [tr8n_samples_wammer_android](https://github.com/tr8n/tr8n_samples_wammer_android)



Where can I get more information?
==================

* Register on Tr8nHub.com: https://tr8nhub.com

* Read Tr8nHub's documentation: http://wiki.tr8nhub.com

* Visit Tr8nHub's blog: http://blog.tr8nhub.com

* Follow Tr8nHub on Twitter: https://twitter.com/Tr8nHub

* Connect with Tr8nHub on Facebook: https://www.facebook.com/pages/tr8nhubcom/138407706218622

* If you have any questions or suggestions, contact us: feedback@tr8nhub.com



