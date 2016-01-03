# Pathfinder User Guide

## Main Screen

As soon as you open pathfinder interface you will be presented with a global overview of your dependency graph

This is a sample of the full dependencies graph of Pathfinder project itself

![Pathfinder UI](docs/images/pathfinder-ui-full.png)

## Project filtering

The first step you will probably want to perform is to filter such graph over the artifacts properties you want to analyze.

Do do so you can use the first filtering group which will filter over main dependencies properties.
With this kind of filtering you will obtain a filter over inner nodes, leaving external dependencies on leaves:
  
![Pathfinder Inner filtering](docs/images/pathfinder-ui-filter1.png)

The second filtering group will acto also on leaf nodes, allowing you to obtain a graph just of your internal packages

![Pathfinder Leaves Filtering](docs/images/pathfinder-ui-filter2.png)

## Identify Snapshots and branches.

These (un)filtered graph can actually provide you with many useful information with the first glance since node coloring will help you identify:

- SNAPSHOT dependencies : marked in RED, are identified by any version string containing "SNAPSHOT" string

- BRANCH dependencies : marked in YELLOW, are identified by any version string containing characters other then the regular expression  [0-9]\*(\.[0-9]\*)\* (some whitelist is needed in future release to handle common "RELEASE" or "Final" tokens)

- RELEASE dependencies : marked in blue, are redular stable release artifact

## Impact Path

Impact Path section will help you identify all dependencies involved whan you are modifying a release artifact.
 
![Pathfinder Impact Path](docs/images/pathfinder-ui-impact.png)

more details here
 