## Verification

WTC-Y3KZ57RS

# DUST-IT

### *Understand it. Practise it. Dust it.*

A cloud-native learning and competency platform. Full product requirements
are in `dust-it-master-requirements.md`. This README covers everything
technical: structure, build order, and setup instructions for every step,
all in one place.

## Structure

```
dust-it/
├── .github/workflows/     CI/CD pipelines (GitHub Actions)
├── backend/               Java Spring Boot API (controllers, services, models...)
├── competency-function/   AWS Lambda - consumes learning events, calculates competency
├── monitoring/            CloudWatch alarms (CloudFormation)
├── frontend/              Plain HTML/CSS/JS
├── database/              schema.sql / seed.sql
├── docker-compose.yml
├── Makefile
└── README.md              (this file)
```

Most of `frontend/` and `database/` are placeholders — filled in as each
build-order step below was reached.

## Build order

```
1. ✅ Spring Boot project + first controller/API
2. ✅ Managed database instead of local Postgres (falls back to local Postgres until configured)
3. ✅ JavaScript frontend calling the API
4. ✅ First real database table: Topics
5. ✅ Resources + object storage integration
6. ✅ Assessments
7. ✅ Message queue + event publishing (learning events)
8. ✅ Serverless competency-calculation function (consumes the queue)
9. ✅ Managed auth (Cognito / Azure AD B2C)
10. ✅ CI/CD pipeline to a managed container service
11. ✅ Monitoring, alarms, secrets management
```

All 11 steps have working code. What's left is genuinely yours: creating
the AWS resources by following each section below, running it end-to-end,
and deciding how much of it to keep running versus spin up only for demos
(see the cost note under CI/CD).

---

## Step 1-2: Running locally

```bash
# 1. Start Postgres
docker run --name dustit-db -e POSTGRES_USER=dustit -e POSTGRES_PASSWORD=dustit -e POSTGRES_DB=dustit -p 5432:5432 -d postgres:16

# 2. Run the backend
make run
# or: cd backend && mvn spring-boot:run

# 3. Check it worked
curl http://localhost:8080/api/health
```

### Connecting to AWS RDS instead of local Postgres

Once you've created a free-tier RDS PostgreSQL instance and it shows
"Available" in the AWS console, connect with environment variables — no
code changes needed:

```bash
export DB_URL=jdbc:postgresql://<your-rds-endpoint>:5432/dustit
export DB_USERNAME=<your-rds-master-username>
export DB_PASSWORD=<your-rds-master-password>

mvn spring-boot:run
```

Leave those variables unset and it falls back to local Postgres
automatically — this same "works locally, real once configured" pattern
repeats for S3, SQS, and Cognito below.

## Step 3-4: Frontend + Topics

Once the frontend is wired up, open `frontend/index.html` in a browser —
it calls `/api/health` and lists topics automatically.

```bash
# List topics (empty at first)
curl http://localhost:8080/api/topics

# Add one
curl -X POST http://localhost:8080/api/topics \
  -H "Content-Type: application/json" \
  -d '{"title": "Goodwill", "description": "How to calculate goodwill in a business combination.", "subject": "Accounting"}'

# List again - your new topic should be there
curl http://localhost:8080/api/topics
```

## Step 5: Resources + S3

1. **Create an S3 bucket** — AWS Console → S3 → Create bucket. Name it
   something globally unique (e.g. `dust-it-resources-yourname`). Keep
   "Block all public access" ON — thumbnails are served via presigned
   URLs, not a public bucket.
2. **Set up AWS credentials locally** — install the AWS CLI, run
   `aws configure`, provide an access key/secret from an IAM user with S3
   permissions (create one in IAM → Users). No keys are ever hardcoded.
3. **Point the app at your bucket**:
   ```bash
   export AWS_REGION=af-south-1
   export AWS_S3_BUCKET=<your-bucket-name>
   ```

```bash
# Add a resource to topic 1
curl -X POST http://localhost:8080/api/topics/1/resources \
  -H "Content-Type: application/json" \
  -d '{"title": "Goodwill Explained", "url": "https://example.com/goodwill", "resourceType": "ARTICLE", "source": "Investopedia"}'

# List resources for that topic
curl http://localhost:8080/api/topics/1/resources

# Upload a thumbnail for resource id 1
curl -X POST http://localhost:8080/api/resources/1/thumbnail \
  -F "file=@/path/to/image.jpg"

# List again - thumbnailUrl should now be a signed S3 link (valid 15 min)
curl http://localhost:8080/api/topics/1/resources
```

## Step 6: Assessments

The full flow: concept → assessment → question → attempt → answer → score.

```bash
# 1. Add a concept to topic 1
curl -X POST http://localhost:8080/api/topics/1/concepts \
  -H "Content-Type: application/json" \
  -d '{"name": "NCI", "description": "Non-controlling interest"}'
# -> note the returned concept id, e.g. 1

# 2. Create a Beginner assessment for topic 1
curl -X POST http://localhost:8080/api/topics/1/assessments \
  -H "Content-Type: application/json" \
  -d '{"title": "Goodwill - Beginner", "difficultyLevel": "BEGINNER"}'
# -> note the returned assessment id, e.g. 1

# 3. Add a question, linked to the concept from step 1
curl -X POST http://localhost:8080/api/assessments/1/questions \
  -H "Content-Type: application/json" \
  -d '{"text": "What does NCI stand for?", "options": ["Net Current Income", "Non-controlling interest", "Net Capital Investment"], "correctOptionIndex": 1, "conceptIds": [1]}'

# 4. Start an attempt
curl -X POST http://localhost:8080/api/assessments/1/attempts \
  -H "Content-Type: application/json" \
  -d '{"studentId": "student-1"}'
# -> note the returned attempt id, e.g. 1

# 5. Submit an answer
curl -X POST http://localhost:8080/api/attempts/1/answers \
  -H "Content-Type: application/json" \
  -d '{"questionId": 1, "selectedOptionIndex": 1}'

# 6. Complete the attempt and get the score + encouraging message
curl -X POST http://localhost:8080/api/attempts/1/complete
```

This step deliberately does NOT calculate competency per concept or
record a learning event yet — that's steps 7-8 below.

## Step 7: SQS + Learning Events

1. **Create a standard SQS queue** — AWS Console → SQS → Create queue →
   Standard. Name it `dust-it-learning-events`.
2. Your IAM user also needs SQS permissions — attach `AmazonSQSFullAccess`
   for now (tighten to a specific queue ARN later).
3. **Point the app at your queue**:
   ```bash
   export AWS_SQS_QUEUE_URL=<your-queue-url>
   ```

```bash
# Complete an attempt - automatically publishes an ASSESSMENT_COMPLETED event
curl -X POST http://localhost:8080/api/attempts/1/complete

# Manually record a different event type
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"eventType": "TOPIC_SEARCHED", "studentId": "student-1", "data": {"query": "how do I calculate goodwill"}}'
```

Check the SQS queue in the AWS Console (Send and receive messages → Poll
for messages) to see the JSON land. If `AWS_SQS_QUEUE_URL` isn't set, the
app still works — it just logs a warning and skips publishing.

## Step 8: Competency Function (Lambda)

The Lambda lives in `competency-function/` — it's a **separate project**,
deliberately not part of the Spring Boot app: it's standalone,
independently-scaling, and independently-failing. If it has a bug,
students can still take assessments; only the competency recalculation is
affected. It's also plain JDBC rather than JPA, keeping it lightweight
for faster cold starts.

### Building

```bash
cd competency-function
mvn package
```

Produces `target/competency-function.jar` — a single "shaded" jar with
all dependencies bundled, which Lambda requires.

### Deploying

1. **AWS Console → Lambda → Create function**
    - Runtime: Java 17
    - Handler: `com.dustit.competency.CompetencyCalculatorHandler::handleRequest`
    - Upload `target/competency-function.jar`
2. **Set environment variables**:
   ```
   DB_URL=jdbc:postgresql://<your-rds-endpoint>:5432/dustit
   DB_USERNAME=<your-rds-username>
   DB_PASSWORD=<your-rds-password>
   ```
3. **Connect the SQS trigger** (Configuration → Triggers → Add trigger →
   SQS → the same queue from step 7)
4. **VPC note**: if RDS isn't publicly accessible, this Lambda needs to
   run inside the same VPC to reach it — extra networking config, fine to
   skip for learning purposes but worth knowing before this handles real
   student data.

### Testing end-to-end

```bash
# Complete an attempt (as in step 6)
curl -X POST http://localhost:8080/api/attempts/1/complete

# Give the Lambda a few seconds, then check the result
curl http://localhost:8080/api/students/student-1/competency
```

Expected result — Section 10 made real, a concept-level score calculated
asynchronously, not just an overall percentage:

```json
[{"conceptId": 1, "conceptName": "NCI", "topicId": 1, "topicTitle": "Goodwill",
  "score": 100.0, "status": "Competent", "updatedAt": "2026-..."}]
```

You can also check AWS Console → Lambda → Monitor → CloudWatch logs for a
line like `Updated competency: student=student-1 concept=1 score=100.0
status=Competent`, or query `SELECT * FROM competency_results;` directly.

**What this deliberately doesn't do**: calculate anything beyond
concept-level score + status. The richer diagnostic text from Section 10
("you struggled with NCI specifically...") would need an AI call
analyzing the wrong answers — a reasonable future feature, not bundled in
silently. It also has no dead-letter queue configured yet for messages
that keep failing.

## Step 9: Cognito Auth

1. **AWS Console → Cognito → Create user pool** (standard
   username/password sign-in; one app client, no secret needed)
2. **Create a test user** (Users → Create user)
3. **Get your issuer URI**:
   ```
   https://cognito-idp.<your-region>.amazonaws.com/<your-user-pool-id>
   ```
4. **Point the backend at it**:
   ```bash
   export COGNITO_ISSUER_URI=https://cognito-idp.<region>.amazonaws.com/<pool-id>
   ```
5. **Getting a token to test with**: AWS CLI's `initiate-auth` with your
   test user's credentials, or the Cognito Hosted UI — either way, grab
   the `IdToken` from the response.

```bash
# Without COGNITO_ISSUER_URI set, everything works exactly as before, no token needed.

# Once it's set, protected endpoints require a real token:
curl -X POST http://localhost:8080/api/assessments/1/attempts \
  -H "Authorization: Bearer <your-id-token>"
# studentId now comes from the token's `sub` claim, not the request body

# Without a token, this now correctly fails:
curl -X POST http://localhost:8080/api/assessments/1/attempts
# -> 401 Unauthorized

# A student can't view someone else's competency by guessing an ID:
curl http://localhost:8080/api/students/someone-elses-id/competency \
  -H "Authorization: Bearer <your-id-token>"
# -> 403 Forbidden, unless the token's subject actually matches
```

Reading endpoints (`GET /api/topics`, `GET /api/assessments/{id}/questions`)
stay open without a token — browsing content doesn't need login, only
actions tied to a specific student's identity do.

## Step 10: CI/CD

Two GitHub Actions workflows deploy automatically on push to `main`:
- `.github/workflows/deploy-backend.yml` — builds the Docker image,
  pushes to ECR, deploys to ECS Fargate
- `.github/workflows/deploy-competency-function.yml` — builds the Lambda
  jar, updates the function code

Both authenticate to AWS via **OIDC federation**, not a stored access
key — GitHub proves its identity per-run and gets a credential that
expires in minutes, instead of a long-lived `AWS_SECRET_ACCESS_KEY`
sitting in GitHub secrets forever.

### One-time setup

1. **Create the OIDC identity provider** (if you don't have one) — AWS
   Console → IAM → Identity providers → Add provider:
    - Provider type: OpenID Connect
    - Provider URL: `https://token.actions.githubusercontent.com`
    - Audience: `sts.amazonaws.com`

2. **Create an IAM role GitHub Actions can assume** — IAM → Roles →
   Create role → Web identity → select the provider above. Trust policy,
   restricted to your specific repo:
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Principal": {
           "Federated": "arn:aws:iam::<AWS_ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
         },
         "Action": "sts:AssumeRoleWithWebIdentity",
         "Condition": {
           "StringEquals": { "token.actions.githubusercontent.com:aud": "sts.amazonaws.com" },
           "StringLike": { "token.actions.githubusercontent.com:sub": "repo:<your-github-username>/<your-repo-name>:*" }
         }
       }
     ]
   }
   ```
   Attach `AmazonEC2ContainerRegistryPowerUser` and `AmazonECS_FullAccess`
   plus a custom statement for `lambda:UpdateFunctionCode` to start, then
   tighten to specific resource ARNs once things work.

3. **Add the role ARN to GitHub** — Repo → Settings → Secrets and
   variables → Actions → New repository secret: `AWS_DEPLOY_ROLE_ARN`

4. **Create the one-time AWS infrastructure the workflows deploy INTO**
   (the pipeline updates these, it doesn't create them):
    - An ECR repository named `dust-it-backend`
    - An ECS cluster named `dust-it-cluster` (Fargate)
    - An ECS service named `dust-it-backend-service`, initially created
      from `backend/ecs/task-definition.json` with placeholders filled in
    - The `dust-it-competency-calculator` Lambda from step 8

### Honest cost note

Fargate is **not** part of AWS's always-free tier the way Lambda is —
there's a limited free allowance for new accounts in their first 12
months, but check AWS's current terms directly rather than trusting this
document, since free-tier offers change. A reasonable approach: run the
ECS service only when you need to demo it live, then scale it to 0 tasks
afterward. The pipeline and Docker setup are what's being evaluated in a
portfolio review — it doesn't need to run 24/7 to prove it works.

## Step 11: Monitoring, Alarms, Secrets

### Deploying the CloudWatch alarms

`monitoring/cloudwatch-alarms.yaml` covers the whole stack:

```bash
aws cloudformation deploy \
  --template-file monitoring/cloudwatch-alarms.yaml \
  --stack-name dust-it-monitoring \
  --parameter-overrides \
      NotificationEmail=you@example.com \
      RdsInstanceId=<your-rds-instance-id> \
  --region af-south-1
```

Confirm the SNS email subscription you'll receive, or notifications go
nowhere.

| Alarm | Catches |
|---|---|
| Competency function errors | Students' competency silently stops updating |
| Events queue backlog | The competency function is falling behind, even without erroring |
| Backend high CPU | The API struggling under load, before it starts timing out |
| RDS high CPU | Database is the bottleneck, not the application |
| RDS low storage | Database will stop accepting writes if not caught early |

### Billing alarm (given the budget constraint)

AWS billing metrics only exist in `us-east-1`, so this is simplest done
directly in console rather than the template above:

1. Billing Console → Billing preferences → enable "Receive Billing Alerts"
2. Switch to `us-east-1` in the console
3. CloudWatch → Alarms → Create alarm → Billing → Total Estimated Charge
4. Set the threshold low ($1-5) so you're warned well before anything
   meaningful is charged
5. Point it at the SNS topic above, or use email notification directly

Worth doing before deploying anything from this project for real.

### Secrets management

Right now the RDS password travels as a plain environment variable when
you `export` it locally — fine for local dev, not for anything deployed.

**No application code changes are needed for the fix.**
`application.properties` already reads `${DB_USERNAME}`/`${DB_PASSWORD}`
as environment variables — it doesn't care whether they were set directly
or injected by ECS from Secrets Manager.

1. **Store the credentials in Secrets Manager** — Console → Secrets
   Manager → Store a new secret (as one secret or two:
   `dust-it/db-username`, `dust-it/db-password`)
2. **Reference them from the task definition** — already wired up in
   `backend/ecs/task-definition.json`:
   ```json
   "secrets": [
     { "name": "DB_USERNAME", "valueFrom": "<SECRETS_MANAGER_ARN_FOR_DB_USERNAME>" },
     { "name": "DB_PASSWORD", "valueFrom": "<SECRETS_MANAGER_ARN_FOR_DB_PASSWORD>" }
   ]
   ```
   Replace the placeholder ARNs with your actual secret ARNs. The ECS
   agent resolves these at container start and injects them as regular
   environment variables — the app never knows the difference.
3. **Grant the ECS execution role permission** — `ecsTaskExecutionRole`
   needs `secretsmanager:GetSecretValue` on those specific ARNs, never a
   wildcard `*`.
4. **Never commit real values** — the placeholders in
   `task-definition.json` stay placeholders in git.

`DB_URL`, `AWS_S3_BUCKET`, `AWS_SQS_QUEUE_URL`, `COGNITO_ISSUER_URI` stay
as plain environment variables on purpose — they're identifiers/URLs, not
credentials. Only actual secrets (passwords, API keys, tokens) belong in
Secrets Manager.

### Logging

Already wired in from earlier steps, nothing extra to do:
- **ECS**: `task-definition.json`'s `logConfiguration` sends logs to
  CloudWatch Logs (`/ecs/dust-it-backend`)
- **Lambda**: logs to CloudWatch automatically — see the
  `context.getLogger().log(...)` calls in `CompetencyCalculatorHandler`