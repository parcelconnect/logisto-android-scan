Logging
-------

Log messages are written using the Timber logging library.

Remote logs are written to using Logentries.

The Logentries account is registered using the "fastway@logistio.ie"
    email address.
    
The access token for Logentries is stored in ie.fastway.scansort.config.SecretProvider.

Logentries will be initialised in `FastwayScanSortApp.onCreate()` if 
`LogConfig.LOGENTRIES` is true.
If so, everything that is written to the log will also be written
to Logentries.





