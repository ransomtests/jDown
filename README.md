# jDown
A java based command line utility to help download files.

The project is still under development so it'll need java(jdk 11 or above) and maven(3.6 or above) to be installed in your system in order to run.

Download the source code and build the project using `mvn clean package` once the build is complete the downloader can be used with the following syntax.

`java -jar jDown.jar <URL> <PATH_TO_DOWNLOAD>`

The downloaded `jar` would be inside the `target` named directory, at the same level of `pom.xml`

The *PATH_TO_DOWNLOAD* is an optional argument and refers to the directory in which the final downloaded file would be stored, if the directory does not exist, the program would attempt to create it.

Depending on the config of your system, the program, on run-time, would decide how many partitions to make of the file you are trying to download. For example, if you have a 4 core CPU with hyper-threading enabled, essentially giving you 8 available cores to work with, the program would split the file into 4 equal parts and then download them in parallel.

Once the download is complete, the files are all sequentially merged into final output file in the directory of your choice (this falls back to the directory from which the utility was invoked).

If the download, for some reason was interrupted, simply re run the command, with the same arguments and it should resume the download from where it was interrupted.

The `SHA-256` and `MD5` checksums and the path to the final downloaded file are printed in the end so that you can validate the integrity of the file that has been downloaded.


Please do open an issue in case you find a bug, or if you can suggest a new add-on to the project.

Example:

`java -jar target\jDown.jar http://thesoundeffect.com/music/mp3/GoodTimes.mp3 downloads`

```
Initializing
Download started
completion 0.687837, elapsed 0, left 0.000000 , bytes 54438, downloadSpeed 0.000000 MB/s
completion 2.979769, elapsed 1, left 32.559649 , bytes 235830, downloadSpeed 0.224905 MB/s
completion 5.316278, elapsed 2, left 35.620307 , bytes 420750, downloadSpeed 0.200629 MB/s
completion 6.397349, elapsed 3, left 43.894424 , bytes 506310, downloadSpeed 0.160952 MB/s
completion 7.426110, elapsed 4, left 49.863999 , bytes 587730, downloadSpeed 0.140126 MB/s
completion 8.193322, elapsed 5, left 56.025307 , bytes 648450, downloadSpeed 0.123682 MB/s
completion 9.431323, elapsed 6, left 57.617797 , bytes 746430, downloadSpeed 0.118642 MB/s
completion 11.050504, elapsed 7, left 56.345527 , bytes 874578, downloadSpeed 0.119152 MB/s
.
.
.
completion 90.112747, elapsed 32, left 3.511069 , bytes 7131858, downloadSpeed 0.212546 MB/s
completion 92.362072, elapsed 33, left 2.728951 , bytes 7309878, downloadSpeed 0.211250 MB/s
completion 94.750891, elapsed 34, left 1.883568 , bytes 7498938, downloadSpeed 0.210340 MB/s
completion 98.586950, elapsed 35, left 0.501656 , bytes 7802538, downloadSpeed 0.212602 MB/s
Download complete. Merging!

Download Time -> 37363
File path -> D:\Workspace\IntelliJ\jdown\downloads\GoodTimes.mp3
calculating checksum
MD5 -> C8ACD9F92FC4AA732B2F7FF9FFA41D10
SHA-256 -> 933385417A5846AC81FF945A0443963231CBCE501E5F497787E40FDC3CF0CD77

```

Explanation of the output:

    1: completion : percentage completion of the download
    2: elapsed : time elapsed in seconds
    3: left : time left in seconds
    4: bytes: number of bytes downloaded
    5: downloadSpeed: estimated download speed in MegaBytes per second
    6: Download Time: time taken to fully download the file, in milli-seconds
    
