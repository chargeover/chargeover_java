chargeover_java
===============

ChargeOver API Wrapper for Java

Configuration
-------------

Look at Configuration/API & Webhooks in your ChargeOver instance for
relevant configuration.

Dependencies
------------

This build is dependant on the Jackson JSON proccessing
libraries. (See: http://jackson.codehaus.org/). The .jar files are
included here for convenience.
See also: http://wiki.fasterxml.com/JacksonInFiveMinutes

Notes
-----

The find...() methods on the ChargeOver objects return java generics
representing the returned JSON. For serious development it may be more
useful to create Java classes for the commonly used objects.