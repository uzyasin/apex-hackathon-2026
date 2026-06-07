Run the full feature pipeline for: $ARGUMENTS

Execute these stages in order. Do not skip ahead.
After each stage, confirm the handoff file exists before starting the next.

1. Delegate to the `planner` subagent with the feature request above.
   Wait for `.pipeline/spec.md` to exist.
   If the spec contains OPEN QUESTIONS, STOP and show them to me before continuing.

2. Delegate to the `coder` subagent.
   Wait for `.pipeline/changes.md` to exist.

3. Delegate to the `tester` subagent.
   Wait for `.pipeline/test-results.md` to exist.
   If tests failed, STOP and show me the failures. Do not proceed to review.

4. Delegate to the `reviewer` subagent.
   Show me the full content of `.pipeline/review.md`.

Report the final verdict (SHIP / NEEDS WORK / BLOCK).
Do not commit or merge anything. Leave changes for human review.
