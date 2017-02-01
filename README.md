# youtube-dl-web

This web application is a tiny wrapper around the [youtube-dl](https://rg3.github.io/youtube-dl) 
and [youtubedl-java](https://github.com/sapher/youtubedl-java) libraries.
It provides an end user functionality to easily download a youtube video or a playlist and eventually 
convert it to the mp3 format.

## Prerequisites

In order to run this application you will need to have the folowing software installed on your 
machine:
* [java] (http://www.oracle.com/technetwork/java/javase/downloads/index.html),
* [youtube-dl](https://rg3.github.io/youtube-dl) and
* [ffmpeg](https://www.ffmpeg.org) or [avconv](https://libav.org) if you need to convert 
from one format to another.

## Usage

* Enter the folder where you cloned [this project]
(https://github.com/zeljko-m-gavrilovic/youtube-dl-web.git) and run the application in terminal with:
`java -jar youtube-converter-0.1.0-SNAPSHOT-standalone.jar`
* Open the web browser and go to the application main page [http://localhost:3000]
(http://localhost:3000)

## Screenshots

## Some background info

There are two reasons why I've spent my spare time doing this project.

The first one is that I needed a GUI application to even more easily use the great library
[youtube-dl](https://rg3.github.io/youtube-dl). It actually already has everything I need. 
There is a CLI and learning just a few options gives you the possibility to easily download 
youtube videos and convert them to mp3 so you can i.e. listen your favourite songs/talks offline 
during the daily routine like driving the car to work and back home. :)

But there is a second reason which preventis me from using some of the already existing GUI applications. 
I needed some playground for learning Clojure. It was a good candidate for a small project where I
could see how does the web developemnt in Clojure differs from the plain/pure Java web development,
is it hard to reuse some already existing code/library and how to use some external not Java native
library like [youtube-dl](https://github.com/zeljko-m-gavrilovic/youtube-dl-web.git).

## License

Copyright © 2016 BigNumbers

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
