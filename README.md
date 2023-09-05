# identity-reconciliation
The identity-reconciliation application is a spring boot application that keeps a track of the email and phone numbers used by an individual 
visiting a site. If a user uses the same email with a different phone number or vice versa, this application lists down all the emails or 
phone numbers associated with the primary email used. 

If the email and phoneNumber isn't present in the persistence layer, it creates a new entry in the database.


The _**persistence layer of this application_** is a mysql instance created and hosted using the [**Amazon AWS RDS**](https://aws.amazon.com/) service.
The backend layer of this application is hosted using [Render](https://render.com/).

# Endpoint for this application :
Endpoint : https://identity-recon-g171.onrender.com/identify  

The endpoint for this application is [here](https://identity-recon-g171.onrender.com/identify).  

However to see the full functionality, we need to pass an email and a phoneNumber as a parameter,  

here's an [example](https://identity-recon-g171.onrender.com/identify?&email=oindrila.banerjee@gmail.com&phoneNumber=159357852) of a request with **email : _oindrila.banerjee@gmail.com_** and **phoneNumber : _112233445566_**

