# WaiterBotApp
This is the first real Android application I wrote as part of a robotics school project.

The application was used to control a robot designed for waiting tables at a restaurant. Orders from customers were sent to a web server which could be accessed in the application. The communication between the application and the web server was simple **http requests**, with data sent as a **json payload**. The communication between the application and the robot was **BLE** (Bluetooth Low Energy). 

As this application was made solely for use with the robot built in this school project, I will not be making any updates to the code. If I did however, there would be a a few things I would focus on improving:

* Using interfaces exclusively to communicate between fragments. As of now some fragments use interfaces, some use other hacky "cheating" methods.

* Reducing undefined behaviour. One example is that the application is fully reliant on the web server being online. Without it, the application will most likely crash when trying to populate the listview in "handleOrdersFragment".

* Implementing proper services for dealing with the BLE connection when the app is running in the background. 

* Implementing proper multi-touch for the seekbars that are used for remote controlling the robot. 

* Making the "Robot Status" functionallity more robust. The robot was sending information on it's status (delivering food, docked in the battery charging station etc) over BLE once every second. The system for this was badly designed, for example the status was sent as a string, rather than a simple byte.

* Making both BluetoothHandler and DbHandler completely static classes, or at least make a singleton out of them. It doesn't really make any sense to create a bluetoothHandler-object, design-wise.

* Putting a limit on how long the application is searching for BLE devices. As of now, if it can't find the current BLE device it will keep searching until terminated. The task of searching for bluetooth devices is one of the most battery-intensive ones you can perform with your phone, so it's easy to realize that there should be a time limit on it.