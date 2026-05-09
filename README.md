## How to Run
1. Start Postgres and Redis:
   docker-compose up -d

2. Run the Spring Boot app:
   ./mvnw spring-boot:run

## Tech Stack
 Spring Boot
 PostgreSQL (stores all posts, comments, users)
 Redis (handles rate limiting and notifications)
## API Endpoints
 POST /api/posts — create a post
 POST /api/posts/{postId}/comments — add a comment
 POST /api/posts/{postId}/like — like a post


## How I Guaranteed Thread Safety

So the main problem I was thinking about was this:
Imagine 200 bots all hitting the same post at the exact same 
millisecond. My code does two things — checks the counter, 
then increments it. If two bots both check at the same time 
and both see 99, they both think they're the 100th comment 
and both get through. Now you have 101 comments. Classic 
race condition.

The fix was using Redis INCR operation instead of checking 
first then incrementing. INCR does both in one single atomic 
step — Redis processes it completely before touching the next 
request. No two threads can interfere with each other.

So what I do is — increment first, then check the returned 
value. If it comes back as 101 or more, I immediately 
decrement it back and reject the request. This way the 
counter never actually stays above 100.

For the cooldown lock I used SET NX (set if not exists) 
with a TTL. This is also atomic — either the key gets set 
or it doesn't. No in between state is possible. If two bots 
try the same cooldown key simultaneously, only one of them 
will get a successful SET NX. The other gets blocked.

Basically I let Redis handle all the concurrency stuff 
because it's single threaded internally. My Spring app can 
have 200 threads running at once but Redis processes each 
command one at a time which is exactly what I needed.

## How the Notification Scheduling Works

Nobody wants 50 notifications from 50 bots. So I batched them.

When a bot comments, I check if the user already got a 
notification in the last 15 minutes. If yes, I push the 
message into a Redis list. If no, I send immediately and 
start a 15 minute cooldown.

Every 5 minutes a scheduled job checks Redis for users with 
pending notifications, grabs all their messages, and logs 
one summarized message — "Bot X and 4 others interacted 
with your posts" — then clears the list.

I used Redis for the pending list instead of a Java list 
because if the server restarts, a Java list gets wiped. 
Redis persists outside the app so nothing gets lost.
