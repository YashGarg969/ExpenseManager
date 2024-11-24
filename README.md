# Expense Manager App

Features:-
1. Google Sign-In:- The first screen is a login screen, which uses firebase google authentication to ensure all the operations being done inside the app are done by the authorized individuals.

2. Transaction Entry:- This feature allows user to save their own entries in the database whether it is an expense or income. For this, I have created "Create Record Screen"
Transaction fields:
-> Date of Transaction
-> Amount
-> Transaction Type:- Income or Expense
-> Description (Optional Field)

3. Dashboard Screen:- This screen will show user's transactions. It is also supported with search box which filters the records on the basis of transactionType and description.
Search Criteria:
-> Transaction Type
-> Transaction Description

4. Transaction Detail Screen:- The purpose of this screen is to allow user to view the details of transaction and can edit their transaction details. This screen also supports the feature of deleting 
a particular record.

Additional Fetaures:-
1. Providing a material DatePicker for the user to select date, which enhances user experience.
2. Providing a bottom navigation bar in dashboard screen.

Technical Aspects:-
1. Used Kotlin Programming Language to develop this application.
2. Used firebase realtime database and firebase google authentication for fulfilling database and authentication related requirements.
3. Followed MVVM architecture pattern and live data to ensure code is maintainable and ensuring a better User Experience, avoiding glitches at runtime.
4. Used co-routines for asynchronous operations to make the app more responsive.


Finally, a heartfelt thanks to you for giving the time to read about the project.























