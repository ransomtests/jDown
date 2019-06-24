# jDown
A java based command line utility to help download files.

The project is still under development so it'll need java(jdk 11 or above) and maven(3.6 or above) to be installed in your system in order to run.

Download the source code and build the project using `mvn clean package` once the build is complete the downloader can be used with the following syntax.

`java -jar jDown.jar <URL> <PATH_TO_DOWNLOAD>`

The *PATH_TO_DOWNLOAD* is an optional argument and refers to the directory in which the final downloaded file would be stored, if the directory does not exist, the program would attempt to create it.

Depending on the config of your system, the program, on run-time, would decide how many partitions to make of the file you are trying to download. For example, if you have a 4 core CPU with hyper-threading enabled, essentially giving you 8 available cores to work with, the program would split the file into 4 equal parts and then download them in parallel.

Once the download is complete, the files are all sequentially merged into final output file in the directory of your choice (this falls back to the directory from which the utility was invoked).

If the download, for some reason was interrupted, simply re run the command, with the same arguments and it should resume the download from where it was interrupted.

The `SHA-256` and `MD5` checksums of the final downloaded file are printed in the end so that you can validate the integrity of the file that has been downloaded.


Please do open an issue in case you find a bug, or if you can suggest a new add-on to the project.
