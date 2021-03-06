Summary
=
Snotice is an app that creates custom notifications on your Android phone using a simple text message protocol.
Snotice intercepts all the text messages received by your phone, and if they contain the special `!SNOTICE` text, it will do something interesting.
Snotice comes preloaded with two custom Notices and one Activity:

* MoneyNotice plays a cha-ching sound, informs you how much you made, and what your total for the day is. It has an accompanying Activity that simply tells you how much you made (for the day), and clears all the notifications generated by Snotice
* UptimeNotice plays two sounds: an annoying alert when a website goes down, or a dial-up modem sound when the site comes back online

It is **your** responsibility to write programs that text message your phone when an event occurs! *Hint:* Cell phone providers usually have a free email to SMS gateway.

Protocol
=
For an SMS to be intercepted it needs to contain the text `!SNOTICE` somewhere (usually near the beginning of the message). Directly after the `!SNOTICE` text, the message needs to contain an integer number. This integer number is a flag that tells Snotice which Notice class should handle the message.
Valid Flag Numbers
-
`1` for MoneyNotice  
`2` for UptimeNotice
Info Fields
-
The rest of the message should contain a list of key, value pairs (keys and value separated by a colon, each pair separated with a newline) that will be passed to the Notice class as a HashMap. The keys are case insensitive (they will all be converted to lower case).
Example
-
After loading Snotice on your phone, send the following text messages to yourself:

    !SNOTICE 1  
    Amount: 9.99  
    
and

    !SNOTICE 2
    URL: http://google.com
    State: down
    
and

    !SNOTICE 2
    URL: http://google.com
    State: up


