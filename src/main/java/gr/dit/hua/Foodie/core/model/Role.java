package gr.dit.hua.Foodie.core.model;

// The two roles stored in the database. Visitors are not listed because
// they are simply users who are not logged in.
public enum Role {

    // Registers publicly, places and tracks their own orders
    CUSTOMER,

    // Manages one store and its orders
    OWNER
}