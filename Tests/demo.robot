*** Settings ***
Documentation    An example suite documentation with *some* _formatting_.
...              Long documentation can be split into multiple lines.

*** Test Cases ***
Demo Test 1
    Log to console   Hello from Robot Framework in Test 1

Demo Test 2
    Skip   msg=Skip test until bug is fixed
    Log to console   Hello from Robot Framework in Test 2

Demo Test 3
    Log to console   This test is failing
    Non existing keyword
