# SKILLS — Universal AI Context File
# ─────────────────────────────────────────────────────
# NASIL KULLANILIR:
#   Cursor/VS Code: Chat'te @SKILLS.md ile referans ver
#   Herhangi chat UI: İçeriğin tamamını ilk mesaj olarak yapıştır, "Hazırım de" yaz
#   Claude Code: Proje bağlamı olarak otomatik yüklenir
# ─────────────────────────────────────────────────────

## HACKATHON MODE: ON

You are an elite senior full-stack developer in a 4-hour AI hackathon.
Every line of code must be complete, correct, and runnable immediately.

---

## PROJECT STACK

**Backend:** Java 21 + Spring Boot 3.2.5
- Build: Maven (`mvn spring-boot:run`)
- HTTP client: `RestTemplate` (Anthropic API'ye direkt çağrı)
- DB: H2 in-memory + `JdbcTemplate`
- PDF parsing: Apache PDFBox 3.x
- Validation: `jakarta.validation` (`@Valid`, `@NotBlank`)
- Lombok: `@Data`, `@AllArgsConstructor`, vb.

**Frontend:** React 18 + Vite + Tailwind CSS

**AI Provider:** Anthropic Claude
- Model: `claude-sonnet-4-6`
- All AI logic lives in `backend/src/main/java/com/hackathon/service/AiService.java`
- System prompt is a `String` constant inside `AiService`

**Database:** H2 in-memory (`jdbc:h2:mem:hackathon`)
- Console: http://localhost:3001/h2-console

---

## NON-NEGOTIABLE RULES

1. **Complete code only.** Never write `// TODO` or leave method bodies empty.
   Every code block must compile and run as-is.

2. **Valid imports only.** Only use packages listed in `pom.xml`.

3. **Error handling everywhere.** Every controller method has try/catch
   or relies on `GlobalExceptionHandler`. Failed AI calls return
   `AiResult.fallback()` — never crash the server.

4. **JSON response contract.** All API responses use `ApiResponse<T>`:
   ```json
   { "success": true, "data": { ... }, "error": null }
   { "success": false, "data": null, "error": "mesaj" }
   ```
   Use `ApiResponse.ok(data)` and `ApiResponse.fail(error)`.

5. **Constructor injection only.** No `@Autowired` on fields.
   ```java
   public ApiController(AiService aiService, DbService dbService) { ... }
   ```

6. **No scope creep.** Build exactly what was asked. Skip auth, logging
   systems, pagination, and "nice-to-haves" unless explicitly requested.

7. **Tailwind only on frontend.** No extra `.css` files. No inline `style={}`
   unless unavoidable.

---

## PROJECT ARCHITECTURE
> See AI_CONTEXT/2_architecture.md for full schema and endpoints.
> Key paths:
> - AI logic: `backend/src/main/java/com/hackathon/service/AiService.java`
> - DB CRUD: `backend/src/main/java/com/hackathon/service/DbService.java`
> - File upload + PDF: `backend/src/main/java/com/hackathon/service/DocumentService.java`
> - API routes: `backend/src/main/java/com/hackathon/controller/ApiController.java`
> - DTOs: `backend/src/main/java/com/hackathon/dto/`
> - Frontend API calls: `frontend/src/api/client.js`
> - Components: `frontend/src/components/`

---

## AI INTEGRATION PATTERN (Java)
```java
@Service
public class AiService {
    private static final String SYSTEM_PROMPT = """
        You are a [ROLE].
        Respond ONLY with valid JSON in this format: {...}
        """;

    @Value("${anthropic.api-key}") private String apiKey;
    @Value("${anthropic.model}") private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiResult analyze(String userInput) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 1024,
                "system", SYSTEM_PROMPT,
                "messages", List.of(Map.of("role", "user", "content", userInput))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.anthropic.com/v1/messages", request, Map.class
            );
            // ... parse response, return AiResult
        } catch (Exception e) {
            return AiResult.fallback();
        }
    }
}
```

---

## CONTROLLER PATTERN
```java
@RestController
@RequestMapping("/api")
public class ApiController {
    private final AiService aiService;

    public ApiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/your-endpoint")
    public ResponseEntity<ApiResponse<YourDto>> yourEndpoint(@Valid @RequestBody YourRequest req) {
        YourDto result = aiService.doSomething(req.getInput());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
```

---

## DB ACCESS PATTERN (JdbcTemplate)
```java
@Service
public class DbService {
    private final JdbcTemplate jdbc;

    public DbService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS ... ");
    }

    public Long save(...) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT ...", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ...);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public List<Map<String, Object>> getAll() {
        return jdbc.queryForList("SELECT ... ORDER BY created_at DESC LIMIT 100");
    }
}
```

---

## 4-HOUR TIME BUDGET REMINDER
- **0:00–0:30** Fill AI_CONTEXT/2_architecture.md, assign tasks
- **0:30–3:00** Code (use /ship command for features)
- **3:00–3:45** Integration, end-to-end test
- **3:45–4:00** Git push, README update

---

## AGENT PIPELINE COMMANDS
- `/ship <feature description>` — runs Planner→Coder→Tester→Reviewer pipeline
- Each agent reads from `.pipeline/` handoff folder
- Pause on OPEN QUESTIONS or test failures — do not auto-continue
