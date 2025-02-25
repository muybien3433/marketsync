db = db.getSiblingDB("subscription");

db.createUser({
    user: "subscription",
    pwd: "subscription",
    roles: [{ role: "readWrite", db: "subscription" }]
});
