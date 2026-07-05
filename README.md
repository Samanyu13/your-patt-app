# System Specification & UX Blueprint: Next-Generation Expense Ledger App

This specification document merges the frictionless, event-first, offline utility of **Splid** with the robust architectural networking and long-term multi-group tracking capabilities of **Splitwise**. It provides a highly detailed, comprehensive feature set and user experience blueprint optimized for ingestion by an AI code generation agent.

---

## Architectural Philosophy & Core Tenets

1. **Zero-Friction Entry:** No mandatory authentication walls (OAuth, email, or phone) to create a group or log an expense.
2. **Offline-First Data Model:** Local database serves as the absolute single source of truth. Synchronizations are performed differentially via Conflict-Free Replicated Data Types (CRDTs).
3. **Graceful Account Escalation:** Local text placeholders (anonymous participants) can be seamlessly merged into authenticated user profiles at any point in the lifecycle without breaking historic ledger data.
4. **Graph-Optimized Settlements:** Native transaction minimization algorithms reduce overall cash transfers across multi-person networks.

---

## Use Case 1: Frictionless Group Creation & Invitation

### 1.1 Core Functional Description
The application allows any user to instantiate an expense-sharing group within three seconds from cold start without an account. The system generates a unique, decentralized cryptographic group identifier (`GroupID`). Members can be initially created as simple local string labels (text placeholders). When a physical user joins this group later via a deep link or QR code, their registered system account safely overrides and merges with the local text placeholder.

### 1.2 Step-by-Step AI Implementation Logic
1. User clicks "Create Group" on landing screen.
2. App queries system locale to pre-select default currency (`BaseCurrency`).
3. App writes a new record to the local database table `groups` with a UUIDv4 string.
4. Generates a deep link string matching the structure: `app://split/group?id=[UUID]&token=[CRYPTO_HASH]`.
5. Provides an API endpoint or internal utility to listen for inbound merge events when an external authenticated user matches an internal placeholder string.

### 1.3 UX/UI Execution Specifications
* **Landing Interface:** Clean, focused hero viewport containing a single prominent action button: `[Create Quick Group]`. Bypasses any signup/login modal.
* **Fields:** Exactly two input fields on creation: *Group Name* (text input, autofocused) and *Base Currency* (dropdown spinner preset to local currency).
* **Onboarding Sheet:** Upon clicking create, a bottom sheet slides up (`height: 45%` of viewport) providing:
  * A native platform share-sheet button containing the deep link.
  * A crisp, high-contrast, scalable SVG QR code for immediate physical scanning.
* **The Smart-Merge Prompt:** If a user invites an external participant who binds to an existing local placeholder name (e.g., "Sarah"), the group administrator receives an administrative card notification:
  > *"Merge placeholder 'Sarah' with registered user Sarah Miller?"* Confirming this triggers a batch transaction updating all foreign keys in the `expenses` table from the placeholder ID to the actual User UUID.

---

## Use Case 2: Flexible & Advanced Expense Logging

### 2.1 Core Functional Description
Supports highly complex transactional data modeling. The database ledger captures arbitrary splits across uneven conditions. The model must handle:
* **Multi-Payer Scenarios:** Multiple individuals contributing differing partial quantities to the total amount paid.
* **Granular Split Configurations:** Equal distribution, exact fixed amounts, variable percentage assignments, fractional shares/ratios, or distinct itemized assignments.
* **Foreign Exchange Engine:** Logging an expense in an arbitrary currency while preserving the group's functional base currency. It automatically fetches a historical conversion rate corresponding to the transaction date, while offering manual adjustments.

### 2.2 Step-by-Step AI Implementation Logic
1. Initialize an `Expense` object with sub-arrays: `PayerAllocation` (mapping UserIDs to amounts paid) and `SplitAllocation` (mapping UserIDs to liability amounts).
2. Validate transaction integrity using an invariant checker: `Sum(PayerAllocation) == TotalAmount` and `Sum(SplitAllocation) == TotalAmount`.
3. If currency differs from group base currency, apply conversion multiplier: `BaseAmount = ForeignAmount * ExchangeRate`.

### 2.3 UX/UI Execution Specifications
* **The Viewport:** Full-screen modal entry sheet featuring a custom numeric keypad layout to bypass platform keyboard discrepancies.
* **The Payer Row:** A horizontally scrollable row of circular avatar chips. Tapping an avatar marks them as the 100% payer. Double-tapping or clicking a dedicated split icon exposes sub-input boxes next to each avatar chip to support multiple payers.
* **The Balancing Input Engine Layout:**

+--------------------------------------------------------+
| [X] New Expense                                 (Save) |
+--------------------------------------------------------+
| Description: [ Alpine Ski Rental           ]          |
| Amount:      [ $ 240.00          ] [ USD v ]           |
+--------------------------------------------------------+
| Paid By:  (x) You ($240)    ( ) Someone Else...        |
+--------------------------------------------------------+
| Split Via:                                             |
|  [ Equal ]  ( Exact )  [ % ]  [ Shares ]  [ Itemized ] |
+--------------------------------------------------------+
| [x] You       [ $ 40.00 ]                              |
| [x] Dave      [ $ 80.00 ] <-- User modified explicitly  |
| [x] Sarah     [ $ 120.00 ] <-- Automatically balanced  |
+--------------------------------------------------------+
| Remaining to balance: $ 0.00                           |
+--------------------------------------------------------+


* **Dynamic Auto-Balancing Interaction:** Under the "Split Via" tab bar, selecting "Exact" shows editable text inputs next to all names. When a user explicitly edits Dave's share, that specific field transitions to a "Locked" state (visualized via a subtle padlock icon and a border highlight). The remaining unassigned financial balance is mathematically distributed in real-time among the remaining unlocked participants. The top-right "Save" button remains disabled, showing an ambient gray color, until `Remaining to balance == 0.00`.

---

## Use Case 3: Offline-First Ledger Synchronization

### 3.1 Core Functional Description
Ensures continuous system utility in zero-network situations (e.g., subways, remote hiking trips, international roaming). The client application relies strictly on its internal database for all transactional reads and writes. Network operations run asynchronously in the background.

### 3.2 Step-by-Step AI Implementation Logic
1. When a user creates/edits a transaction offline, persist it locally with a metadata flag: `sync_status = PENDING` and a incremented Lamport timestamp or vector clock.
2. Initialize an OS-level background sync worker triggered by network availability.
3. Upon connection, exchange state vectors with the remote server. Reconcile modifications using a state-based CRDT resolution policy (e.g., Last-Write-Wins based on explicit high-resolution vector clock timestamps).

### 3.3 UX/UI Execution Specifications
* **Ambient State Tracking:** A small, desaturated status badge is positioned in the header toolbar next to the group name. When offline, it displays an amber cloud outline icon alongside the text `"Saved Offline"`.
* **Pending States:** Any expense item logged during an offline state renders in the primary timeline feed with 75% opacity and a distinct dashed left-border marker.
* **Sync Handshake UI:** Once web connectivity returns, the amber cloud icon triggers an animated spinning sequence, transitioning into a solid green checkmark that fades out gracefully over 2.5 seconds. Items in the timeline smoothly animate to 100% opacity, removing the dashed borders to convey database parity. If a data conflict happens, a clear modal pops up presenting a side-by-side comparison screen allowing manual select-to-override choice.

---

## Use Case 4: Real-Time Receipt Scanning & Itemization

### 4.1 Core Functional Description
Automates receipt entry via on-device or cloud-based optical character recognition (OCR). The application processes raw image payloads, isolates discrete line-items, prices, taxes, and tips, and enables interactive line-by-line distribution to group members.

### 4.2 Step-by-Step AI Implementation Logic
1. Capture image through device camera stream or system document picker.
2. Process image via OCR pipeline returning structured JSON containing bounding boxes and parsed values text fields: `[ { "item": "Truffle Fries", "price": 14.50 }, ... ]`.
3. Compute a global scale coefficient for proportional overheads: `OverheadFactor = TotalBill / Subtotal`.
4. Append `ItemPrice * OverheadFactor` to any user selected for that line-item.

### 4.3 UX/UI Execution Specifications
* **Camera Capture Flow:** Viewfinder layout features real-time edge detection boundaries overlaid as a translucent neon-blue polygon. High-speed haptic pulses click when proper document alignment and focus are achieved, initiating self-capture.
* **The Item Assignment Split-Screen Layout:**


+----------------------------------------------------+
| [X] Assign Items                        [ Proportional ]|
+----------------------------------------------------+
|  (Sarah)   (Dave)   (You)                          |
+----------------------------------------------------+
| [x] Truffle Fries ............. $14.50  [S] [D]    |
| [ ] Ribeye Steak .............. $42.00  [D]        |
| [x] Craft Beer ................ $ 8.00  [S]     [Y]|
+----------------------------------------------------+
| Subtotal: $64.50  Tax/Tip: $12.00                  |
| Sarah: $18.52   Dave: $34.33   You: $3.65          |
+----------------------------------------------------+


* **Interaction System Design:** The interface scales to a split screen. The top quadrant presents horizontally scrollable participant avatar bubbles acting as "color brushes". Selecting a user's bubble sets that user as active. The user then simply taps individual line items below. Tapping a row instantly applies a colored background tint matching the user's color scheme and prints their initials next to the item value. Tapping a second user avatar and clicking the same row divides that item evenly among them. Running balances update continuously at the bottom sheet footer.

---

## Use Case 5: Advanced Debt Simplification & Settle-Up

### 5.1 Core Functional Description
Calculates balances and applies a network optimization algorithm (e.g., maximum flow or matching algorithms over a directed graph) to eliminate redundant transactions. It facilitates payments by launching deep links into localized transactional applications based on client region metadata.

### 5.2 Step-by-Step AI Implementation Logic
1. Aggregate all net credits/debits per participant into a fixed single-column array: `NetBalances`.
2. Sort array to isolate two distinct vectors: `Debtors` (negative balance) and `Creditors` (positive balance).
3. Sequentially match the largest debtor to the largest creditor, creating optimized ledger clearing nodes until all balances evaluate to zero.
4. Construct native platform deep links (e.g., Venmo: `venmo://paycharge?recipient_id=[ID]&amount=[AMOUNT]&note=[NOTE]`, Zelle, or localized UPI schemas).

### 5.3 UX/UI Execution Specifications
* **The Balances Dashboard Layout:** Split into two clean, structured lists: *Who Owes Money* and *Who Is Owed*. A clear animated toggle switch transitions between `[Raw Matrix View]` and `[Simplified Matrix View]`.
* **Settle-Up Matrix Cards:** Tapping a **"Settle Up"** action triggers a crisp focal modal detailing a simplified payment card, e.g., *"You owe Dave $34.50"*.
* **Payment Integration Interface:** Directly below the settlement instruction, the application renders a horizontal carousel of payment app shortcuts (e.g., Zelle, Venmo, Revolut, GPay) dynamically populated by verifying app package presence on the host operating system. Selecting an option launches the target payment application. Upon returning to the main ledger app, a modal confirmation sheet asks: *"Mark this balance as paid?"* Selecting confirmation writes the final settlement item to the distributed data ledger.

---

## Use Case 6: Long-Term Multi-Group Management

### 6.1 Core Functional Description
Provides users with a centralized workspace to manage several distinct expense groups concurrently. It aggregates accounting data globally across all connected groups to generate a consolidated net balance statement, providing a macro view of personal accounts.

### 6.2 Step-by-Step AI Implementation Logic
1. Query `groups` table to pull all records containing the current user's participant identifier.
2. Execute a cross-group aggregation query summing the current user's net position across all separate group ledgers.
3. Return an array of active group objects containing names, metadata, user statuses, and computed financial states.

### 6.3 UX/UI Execution Specifications
* **Dashboard Tab Layout:** A fixed bottom navigation layout split cleanly into three primary operational interfaces: `[Groups Workspace]`, `[Global Ledger]`, and `[User Settings]`.
* **Group Feeds:** Individual groups display as high-contrast list cards. Each card displays the group's title, user avatars, and an explicitly colored state indicator detailing the user's situational standing:
  * **Emerald Green Text:** Bold green lettering displaying *"You are owed $120.00"*
  * **Crimson Red Text:** Deep red lettering displaying *"You owe $45.10"*
  * **Muted Charcoal Text:** Balanced neutral text stating *"Settled"*
* **The Aggregate Account Card:** Anchored to the top viewport header is an immutable total balance summary card displaying a user's net worth across the entire application ecosystem. Swiping horizontally across this banner alters the rendering metric view from total outlays to total amounts owed, and historical categorical spending metrics illustrated via clean, interactive SVG bar graphs.