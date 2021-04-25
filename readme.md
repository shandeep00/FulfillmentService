Exposes an API for fulfillment service which accepts order details and customer’s location [lat/long] and assigns a physical store to fulfil the order with minimum turn around time(TAT).
This is a springboot REST application deployed on heroku backed by postgresql database.

API : https://fulfillmentservice.herokuapp.com/service/createorder?speed=<speed_value>
speed is distance travelled by delivery person in KM per minute.

API : https://fulfillmentservice.herokuapp.com/service/getorder?orderid=<order_id>
Get order details using orderid

Input : JSON request with customer and order details
Example :
{
   "name" : "<customer_name>",
   "latitude" : <customer_latitude>,
   "longitude" : <customer_longitude>,
   "cartItems" : [
		{
			"itemId" : "<itemid>",
			"quantity" : <quantity>
		},
		{
			"itemId" : "I0007",
			"quantity" : 2
		},
		.
		.
		.
		.
		.
	]
}