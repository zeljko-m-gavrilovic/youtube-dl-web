# youtube-converter

This web application is a tiny wrapper around the [youtube-dl](https://rg3.github.io/youtube-dl) and [youtubedl-java](https://github.com/sapher/youtubedl-java) libraries.
It enables an end user to download a youtube video or playlist and eventually convert them to the mp3 format.

## Prerequisite
In order to run this application you will need to have the folowing software:

* [java] (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [youtube-dl](https://rg3.github.io/youtube-dl)
* [avconv](https://libav.org) or [ffmpeg](https://www.ffmpeg.org)

## Usage
* Go to the youtube-dl-web folder and run the application with
`java -jar youtube-dl-web-uberjar.jar`
* Open the browser and go to the application
`http://localhost:3000/`

## Screenshots

## Intention 

My main intention was to create a GUI application to even more easily use the great library
[java] (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
which was actually doing the whole job I needed: donwnloading the youtube videos and converting 
them to mp3 so I can listen them offline while I'm drive to work and back home. :)

The second reason to start this (few lines) project was to have some playground for learning the Clojure
and see how does it differ from (plain) Java to create a web application.

## License
Copyright © 2016 BigNumbers
Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
