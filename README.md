# In-IDE ChatGPT coding sample

This IntelliJ plugin uses ChatGPT to generate JavaDoc for a given method
implementation.

## Installation

1. Download Zip file from `distrib/`.
1. On IntelliJ, open the Setting dialog and navigate to the "Plugins" tab.
1. Click the gear icon and select "Install Plugin from Disk...".
1. Select the downloaded Zip file.
1. Restart IntelliJ.

## Using the plugin

1. On IntelliJ, open a project containing Java source code.
1. With a Java file open, right click on the name of a method.
1. On the context menu, select "Add Javadoc with ChatGPT" (this option might be
   disabled while the IntelliJ project is still loading).

## Samples

A good sample Java file to test the plugin over can be found in
`samples/KnightsTour.java`.
