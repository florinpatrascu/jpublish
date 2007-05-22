This is the JPublish - DWR integration module.

To test the application, run: ant dist, and go to the dist/jpublishdwr folder. This folder
contains a test application containing the following DWR demos:

    * chat - simple chat utility
    * asmg - Anti-spam mailto: Link Creator
    * clock - a clock demo
    * simpletext - Simple Text Generation Demo
    * AddressLookup - AddressLookup and the low level test of it

I tested this application with Jetty 5.1.10. Before, running it, please edit the jetty script I provided
and modify the JETTY_HOME env variable to represent your local Jetty server installation.
On my laptop I have:

    * JETTY_HOME="/usr/local/java/servers/jetty"

Run: ./jetty (don't forget to chmod u+x the jetty script) and point your browser to http://localhost:8080

This test is using DWR 2.0 instead of 2.0.1 because I found reverse AJAX broken with the new DWR and this
is why I rolled back the DWR library. Please check the CHANGELOG.txt for updates.

[2007.05.21] florin