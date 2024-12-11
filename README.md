Real-Time Event Ticketing System
Introduction
The Real-Time Event Ticketing System is a concurrent Java application designed to simulate a dynamic ticket sales process. It demonstrates sophisticated thread synchronization and concurrency management techniques, providing a realistic model of ticket distribution between a vendor releasing tickets and customers purchasing them.
The system features:

Configurable ticket release and retrieval rates
Thread-safe ticket pool management
Flexible configuration via CLI and JSON file storage
Detailed logging and monitoring of ticket transactions

Prerequisites
System Requirements

Java Development Kit (JDK) 11 or higher
Gradle or Maven for dependency management

Dependencies

Google Gson library (for JSON configuration)
Java Util Concurrent libraries

Setup Instructions
1. Clone the Repository
bashCopygit clone https://github.com/yourusername/real-time-ticketing-system.git
cd real-time-ticketing-system
2. Install Dependencies
Using Gradle:
bashCopygradle build
Using Maven:
bashCopymvn clean install
3. Compile the Project
bashCopyjavac -cp .:gson-2.8.9.jar Main.java Configuration.java Customer.java Vendor.java TicketPool.java
Usage Instructions
Running the Application

Launch the application:

bashCopyjava -cp .:gson-2.8.9.jar Main

Configuration Process
When you start the application, you'll be prompted with configuration options:


Load Previous Configuration: Choose to load a previously saved configuration from config.json
Manual Configuration: Enter new configuration parameters:

Total Number of Tickets
Ticket Release Rate (milliseconds per ticket)
Customer Retrieval Rate (milliseconds per ticket)
Maximum Ticket Capacity in the system




Configuration Options

Total Tickets: Total number of tickets to be released
Ticket Release Rate: Time between ticket releases by the vendor
Customer Retrieval Rate: Time between customer ticket purchase attempts
Maximum Ticket Capacity: Maximum number of tickets that can exist in the pool simultaneously


Configuration Saving

You can choose to save your configuration to config.json for future use



System Workflow

Vendor Thread:

Releases tickets into the ticket pool at specified intervals
Stops when all tickets have been released


Customer Thread:

Attempts to purchase tickets from the pool at specified intervals
Stops when all tickets have been processed



Logging
The system provides console logging to track:

Ticket pool state
Vendor and customer activities
Timestamps for each transaction

Example Configuration
CopyTotal Tickets: 100
Ticket Release Rate: 50 milliseconds/ticket
Customer Retrieval Rate: 75 milliseconds/ticket
