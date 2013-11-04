# Fluorite Logger for Eclipse #

## How to Install ##

### Using Eclipse Update Site ###

Update site URL: [http://www.cs.cmu.edu/~NatProg/eclipse/updates/releases](http://www.cs.cmu.edu/~NatProg/eclipse/updates/releases)

### Manual Download ###

Download the latest version of the jar file (under the Releases page) and put it in the 'plugins' directory under the Eclipse installation.

## Description ##

Project main page: [http://www.cs.cmu.edu/~fluorite/](http://www.cs.cmu.edu/~fluorite/ "Fluorite project page")

Fluorite stands for 'Full of Low-level User Operations Recorded In The Editor'.

Fluorite captures not only the events and their IDs, but also includes more detailed information such as the inserted and deleted text and the specific parameters for each command. This enables the detection of many usage patterns that could otherwise not be recognized, such as "typo correction" that requires knowing that the entered text is immediately deleted and replaced. Moreover, the snapshots of each source code file that has been opened during the session can be completely reproduced using the collected information. We also provide analysis and visualization tools which report various statistics about usage patterns, and we provide the logs in an XML format so others can write their own analyzers. Fluorite can be used for not only evaluating existing tools, but also for discovering issues that motivate new tools.