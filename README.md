# WaiterBotApp
This is the first real Android application I wrote as part of a robotics school project.

The application was used to control a robot designed for waiting tables at a restaurant. Orders from customers were sent to a web server which could be accessed in the application. The communication between the application and the web server was simple **http requests**, with data sent as a **json payload**. The communication between the application and the robot was **BLE** (Bluetooth Low Energy). 

As this application was made solely for use with the robot built in this school project, I will not be making any updates to the code. If I did however, there would be a a few things I would focus on improving:

* Using interfaces exclusively to communicate between fragments. As of now some fragments use interfaces, some use other hacky "cheating" methods.

* Reducing undefined behaviour. One example is that the application is fully reliant on the web server being online. Without it, the application will most likely crash when trying to populate the listview in "handleOrdersFragment".

* Implementing proper services for dealing with the BLE connection when the app is running in the background. 

* Implementing proper multi-touch for the seekbars that are used for remote controlling the robot. 

* Making the "Robot Status" functionallity more robust. The robot was sending information on it's status (delivering food, docked in the battery charging station etc) over BLE once every second. The system for this was badly designed and as a design the view displaying this information in the "handleOrdersFragment" felt buggy. 
