* Calendar objects can be stores in a process-global map by `SolarDataIntentSerfice` and  used by the `AlarmIntentService`. 
** This may be more efficient than passin it around in intents.
** Using a wrapper to sync access may be better

* MainService should check current time/date and resend intent to compute the solar data

