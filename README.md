# WARNING: This is some old stuff!
Don't expect this to be good or to work.

## StateVM (created in 2016)
This is a small program that reads a "state file" and turns it into a deterministic finite automaton.
It then reads in some input and runs it through the automaton.

## State-file syntax

    state A{
        1: B;
        2: A;
        default: C;
        start_state;
    }
    
    state A{
        2: B;
        1: A;
        default: C;
        accept_state;
    }
    
    state C{
        default: C;
    }

This declares three states, called A, B and C.
Exactly one state has to be declared as the "start_state".
The automaton will be in this state before reading any input.
Multiple states can be declared as an "accept_state".

State changes are declared with the colon syntax:

    <input-character>: <new state name>;

The input character has to be a single character!
If no cases match, the default case, if one is declared, will be used instead.
The following two special states exist: back and loop.
When using loop the automaton will stay in its current state.
When using back the automaton will go back to its previous state, if one exists.

One the automaton has processed the input without errors it will print the current state's name with one of the following prefixes:
- IAS (in accepting state)
- NAS (not in accepting state)

The provided test file is an automaton that checks if the input is an even binary number.