# Smart Money Challenge

This is a spring boot application developed as a demonstration for Smart Money admission challenge. It consists of an API that receives ATM machines withdrawals requests.

All operations are implemented according to ACID model and are guaranteed to maintain data consistent in a multi-thread or multi-process execution environment.

## Running the server

As requested, the distribution has been embedded in a Docker container. 

Extract the zip file, in the root folder you will find `install.sh` and `install.bat` scripts to build and run the container with a single command, please run them from the root folder in the command line. 

The server will be started automatically with the container on port 8080 and mirrored to the Host machine. Subsequent execution can be done running 

    sudo docker start smartmoney_challenge
    
## Guide

There are two REST Resources, `User` and `Withdraw`, operated through three end-points each:

    User
    
    create:	POST /api/users
      find:	GET /api/users/{code}
      list:	GET /api/users

	Withdraw
	
    create:	POST /api/withdrawals
      find:	GET /api/withdrawals/{code}
      list:	GET /api/withdrawals
      

### Create User 

On successful result a 'code' property is generated, it is a unique key of integer type.

	Request: POST /api/users
	 
    {
    	"name": "Test",
    	"email": "test@test.com"
    }

    Response:
	 
    {
		"code" : 101,
		"email" : "test@test.com",
		"name" : "Test"
	} 

### Find User

You query an User by supplying it's code

    Request: GET /api/users/555

    Response:
    
    {
		"code" : 555,
		"email" : "test@email.com",
		"name" : "test"
	}

### List Users

    Request: GET /api/users

	Response:
	
    [
    	{
			"code" : 1,
			"email" : "test@email.com",
			"name" : "Test 1"
		},
		{
			"code" : 2,
			"email" : "test2@email.com",
			"name" : "Test 2"
		}
	]

### Create Withdraw 

To create a Withdraw you must provide the associated User code OR email.

The withdraw value must have a maximum scale of 2 decimal places, otherwise a validation error is returned.

On successful result, a fee will be calculated with a maximum scale of 5 decimal places, it will be rounded if necessary. 

A code key is also generate to identify the newly created Withdraw.
	
	Request: POST /api/withdrawals
	
	{
		"value" : 50,
		"user" : {
			"email" : "test@email.com"
		}
	}

	Response:
	
	{
		"code" : 151,
		"createdDate" : "2020-12-01T07:25:53.534Z",
		"value" : 50,
		"fee" : 1.5,
		"user" : {
			"code" : 101
		}
	}

### Find Withdraw

You can query a Withdraw by supplying it's code

    Request: GET /api/withdrawals/151

    Response:
	 
	{
		"code" : 151,
		"createdDate" : "2020-12-01T07:25:53.534Z",
		"value" : 50,
		"fee" : 1.5,
		"user" : {
			"code" : 101
		}
	}

### List Withdrawals

    Request: GET /api/withdrawals

	Response:
	
	[
		{
			"code" : 151,
			"createdDate" : "2021-04-11T01:41:58.927Z",
			"value" : 33.34,
			"fee" : 1.00020,
			"user" : {
				"code" : 101
			}
		}, 
		{
		  	"code" : 201,
		  	"createdDate" : "2021-04-10T03:42:01.967Z",
		  	"value" : 33.34,
		  	"fee" : 1.00020,
		  	"user" : {
		    	"code" : 555
		  	}
		}
	]


