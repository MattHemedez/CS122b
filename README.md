# cs122b-spring18-team-55

### Script Explanation
***
The script is written in java and thus in order to run it you have to use the class file which in this case is titled SimpleAverageScript.class. To run it all you have to do is write out the below where [args] is any number of paths to files that contain values to get the average of.
```
C:\Your\Directory> java \path\to\SimpleAverageScript [args]
```

**A simple example is shown below where a file called fileToRead.mat is read.**

```
C:\Your\Directory> java \Path\to\SimpleAverageScript fileToRead.mat
File Name: fileToRead.mat
	File Path: C:\Your\Directory\fileToRead.mat
    Number of entries: 13260
    Successfully calculated sum value
End Results
~~~~~~~~~~~~
Average: 1992245.0 nano seconds
Sum: 26417179323
Total Entries: 13260
```
The output is formatted such that it'll repeat what file was read, the number of lines or entries in the file, and whether it calculated the the sum successfully. It takes in all entries as a java long primitive type which means it can store 64 bits of data.

The End Results section shows the total average of the file and the total sum along with the total number of entries found/read.


**Another example showing how to average multiple files.**

```
C:\Your\Directory> java \Path\to\SimpleAverageScript fileToReadOne.mat fileToReadTwo.mat

File Name: fileToReadOne.mat
        File Path: C:\Your\Directory\fileToReadOne.mat 
        Number of entries: 13260
        Successfully calculated sum value
File Name: fileToReadTwo.mat
        File Path: C:\Your\Directory\fileToReadTwo.mat
        Number of entries: 13260
        Successfully calculated sum value
End Results
~~~~~~~~~~~~
Average: 1992245.0 nano seconds
Sum: 52834358646
Total Entries: 26520
```
For each, file inputted it will show the file name, file path, number of entries read, and a success or error message. The end results shows the overall average and the overall sum along with the total number of entries.
