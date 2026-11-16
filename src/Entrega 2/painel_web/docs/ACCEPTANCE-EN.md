# Administrative panel: architecture and acceptance

## Integration update

A separate integration page and backend meeting module are now prepared, with Android meeting QR and an account-scoped offline queue. They have not been deployed or verified on a real PostgreSQL instance and Android device. The standalone features described below remain independent. See INTEGRACAO-FUTURA.md and the backend ENCONTROS.md for the new contract.

## Scope

The panel is a standalone Node.js HTTP application with a static HTML, CSS and JavaScript interface. It requires Node.js 22 or newer and has no external runtime packages to install. A vendored QR library retains its license notices.

Browser requests go to the panel server. The server holds administrator credentials and a Supabase secret key. It stores panel collections in the `principal` row of `public.painel_web_dados`; the browser never receives the database key. Updates use the previous timestamp to detect concurrent changes.

The existing application API is read through `GET /courses` only. Importing creates panel copies. Changes to panel students, attendance, messages and announcements are not synchronized with the Android application. The current delivery therefore does not yet meet the PDF requirement for a fully integrated system.

## Running and testing

Run `npm run setup` only when creating local configuration, fill the server environment variables described in the main README, and run `npm start`. Never publish the `.env` file. Run `npm test` for isolated HTTP and import-button tests. Tests ignore local credentials and simulate database operations and upstream course responses.

## Acceptance checklist

1. An unauthenticated request cannot access protected collections.
2. Correct administrator credentials create a session; logout invalidates it.
3. A new database starts empty, with no invented students or attendance.
4. Course import handles unavailable services without replacing stored data, and restores the import button after success or failure.
5. A valid course, enrolled active student and meeting allow one attendance per student and meeting.
6. Duplicate attendance and attendance in a closed meeting are rejected; reopening allows a valid new attendance.
7. Deleting a course with meetings is blocked; allowed deletion cleans enrollment references.
8. A stale database version results in a conflict instead of overwriting newer data.
9. Database failure is reported without claiming the operation succeeded.
10. Hosted login, persistence after restart, actual camera scanning and Android synchronization require separate acceptance checks in an authorized environment.

## Delivery limitations

Sessions are in memory and there is one shared team account. Individual administrator roles and a complete audit trail are not implemented. Data is stored as a JSON document rather than a complete relational domain model. Database access checks do not establish row-level security correctness, backup availability or restore success. The supplied SQL inspection scripts must be reviewed and run by an authorized database owner.

The original attendance API does not identify meetings. The new meeting API and Android code are prepared separately. Deployment, migration validation and an actual device end-to-end test are still required before claiming completion.
