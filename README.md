ChargeOver Java API
===================

This is a Java library for the [ChargeOver recurring billing platform](http://www.chargeover.com/). ChargeOver is a billing platform geared towards easy, automated, recurring invoicing. 


Use ChargeOver to:

* painlessly automate your recurring invoicing 
* allow your customers to log in to a customized portal to view and pay their bills online
* automatically follow up on late and missed payments
* build developer-friendly billing platforms (use the ChargeOver REST APIs, code libraries, webhooks, etc.)
* sync customer, invoice, and payment information to QuickBooks for Windows and QuickBooks Online


ChargeOver developer documentation:

* REST API: https://developer.chargeover.com/apidocs/rest/
* Webhooks: https://developer.chargeover.com/apidocs/webhooks/
* Example code: https://github.com/chargeover/chargeover_java/blob/master/src/ChargeOver_Demo.java


ChargeOver developer account sign-up:

* https://chargeover.com/signup/


ChargeOver main documentation:

* http://chargeover.com/docs/


ChargeOver API access in other programming languages:

* https://github.com/chargeover/


Dependencies
------------

This build is dependant on the Jackson JSON proccessing libraries. (See: http://jackson.codehaus.org/). The .jar files are
included here for convenience. See also: http://wiki.fasterxml.com/JacksonInFiveMinutes

Other Notes
-----------

The find...() methods on the ChargeOver objects return java generics representing the returned JSON. For serious development it may be more
useful to create Java classes for the commonly used objects.
