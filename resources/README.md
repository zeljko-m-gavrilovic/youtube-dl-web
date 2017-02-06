# About [youtube-dl-web](https://github.com/zeljko-m-gavrilovic/youtube-dl-web.git)

[This](https://github.com/zeljko-m-gavrilovic/youtube-dl-web.git) web application is a tiny wrapper around the [youtube-dl](https://rg3.github.io/youtube-dl) 
and [youtubedl-java](https://github.com/sapher/youtubedl-java) libraries.
It provides an end user functionality to easily download a youtube video or a playlist and eventually 
convert it to the mp3 format.

## Prerequisites

In order to run this application you will need to have the folowing software installed on your 
machine:
* [java](http://www.oracle.com/technetwork/java/javase/downloads/index.html),
* [youtube-dl](https://rg3.github.io/youtube-dl) and
* [ffmpeg](https://www.ffmpeg.org) or [avconv](https://libav.org) if you need to convert 
from one format to another.

## Run the application

From the dist folder of [this project](https://github.com/zeljko-m-gavrilovic/youtube-dl-web.git) run the application in a terminal with:

    java -jar youtube-dl-web-0.1.0-SNAPSHOT-standalone.jar

Open the web browser and go to the application main page 
[http://localhost:3000](http://localhost:3000). In case the port 3000 is already occupied, application will try to use 
the next available free port i.e. 3001.

## Usage

There are basically few functionalities supported: 
* adding a new track info,
* listing the already entered tracks, 
* downloading and converting tracks,
* deleting the track.


Adding a new track is easy. From the main page of the application click on the button "new track". Then enter the value for the mandatory field url. 
Other fields are optional. Submit the form and you will be redirected back to the main page where you can see the track you've just added. 
From the main page you can preview tracks, download or delete them. Tracks are downloaded into adequate sub folder(s) of the `downloads` folder where you
started the application.

Note that there are checkboxes "download" and "convert to mp3" on the page where you can add a new track. Checking the option "download" downloads the
track/playlist automaticaly after the track is persisted. Checking the option "convert to mp3" will convert your video to audio track.


## Screenshots

GUI part of the application is a matter of a fast change so the screenshots can very soon be out of date but here are 
few of them just to get a "feeling" how does it look:
![Form screenshot](https://github.com/zeljko-m-gavrilovic/youtube-dl-web/blob/master/resources/public/form-screenshot.png?raw=true "Form to enter new track")
![List screenshot](https://github.com/zeljko-m-gavrilovic/youtube-dl-web/blob/master/resources/public/list-screenshot.png?raw=true "List of entered tracks")

## Develepors reminder

A few notes for developers:

* Clone the repository i.e. type the following git command in a terminal:

```
    git clone https://github.com/zeljko-m-gavrilovic/youtube-dl-web.git ~/youtube-dl-web 
```
* Run the tests:

```
    lein test
```

* Start the application:

```
    lein ring server
```

## Some background info

There are two reasons why I've spent my spare time doing this project.

The first one is that I needed a GUI application to even more easily use the great library
[youtube-dl](https://rg3.github.io/youtube-dl). It actually already has everything I need. 
There is a CLI and learning just a few options gives you the possibility to easily download 
youtube videos and convert them to mp3 so you can i.e. listen your favourite songs/talks offline 
during the daily routine like driving the car to work and back home. :)

But there is a second reason which prevented me from using some of the already existing GUI applications. 
I needed some playground for learning Clojure. It was a good candidate for a small project where I
could see how does the web development in Clojure differs from the plain/pure Java web development and
to see if it is easy to reuse some of the already existing Java libraries or an external libraries which are not Java native 
like [youtube-dl](https://github.com/zeljko-m-gavrilovic/youtube-dl-web.git).

## License

Copyright © 2016 BigNumbers

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
