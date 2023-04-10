# GraphTheoryProject-Scheduling
Project Name: Scheduling Graph
Description
This program reads a constraint table from a .txt file, builds a graph corresponding to the constraint table, and checks if the graph satisfies the properties necessary for it to be a scheduling graph. If the properties are satisfied, the program computes the earliest date calendar, the latest date calendar, the floats, and critical path(s). It can import any constraint table constructed in the specified format and transform it into a graph in matrix form. The program loops through the constraint tables provided and stores them in memory, displaying the corresponding graph in matrix form from memory, not directly from the .txt file.

Features
1. Reading a constraint table presented in a .txt file and storing it in memory
2. Displaying the corresponding graph in matrix form (value matrix). Warning: the display must be done from memory, not directly from reading the .txt file. The graph must contain the two fictitious tasks a and w (labeled 0 and N+1 where N is the number of tasks).
3. Checking the necessary properties of the graph such that it can serve as a scheduling graph
  - no cycle,
  - no negative edges.
  If those properties are satisfied, the program computes the calendars:
4. Computing the ranks for all vertices
5. Computing the earliest dates, the latest dates, and the floats.
  For the computation of the latest dates, assume that the latest date of the end of project coincides with its earliest date.
6. Computing the critical path(s) and displaying it or them

Constraints
The program should be able to import any constraint table constructed in the specified format. Here is an example of the format:
- 1 9
- 2 2
- 3 3 2
- 4 5 1
- 5 2 1 4
- 6 2 5
- 7 2 4
- 8 4 4 5
- 9 5 4
- 10 1 2 3
- 11 2 1 5 6 7 8
The N tasks are numbered from 1 to N. The fictitious task a will be denoted as 0. The fictitious task w will be numbered N+1.
