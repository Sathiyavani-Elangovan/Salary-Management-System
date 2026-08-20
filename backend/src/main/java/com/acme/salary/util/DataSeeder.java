package com.acme.salary.util;

import com.acme.salary.model.Employee;
import com.acme.salary.model.User;
import com.acme.salary.repository.EmployeeRepository;
import com.acme.salary.repository.UserRepository;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class DataSeeder implements ApplicationEventListener<ServerStartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DataSeeder.class);
    private static final Random random = new Random();

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Sample data arrays
    private static final String[] FIRST_NAMES = {
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
        "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
        "Thomas", "Sarah", "Christopher", "Karen", "Charles", "Lisa", "Daniel", "Nancy",
        "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra", "Donald", "Ashley",
        "Steven", "Kimberly", "Andrew", "Emily", "Paul", "Donna", "Joshua", "Michelle",
        "Kenneth", "Carol", "Kevin", "Amanda", "Brian", "Melissa", "George", "Deborah",
        "Timothy", "Stephanie", "Ronald", "Rebecca", "Edward", "Sharon", "Jason", "Laura",
        "Jeffrey", "Cynthia", "Ryan", "Dorothy", "Jacob", "Amy", "Gary", "Kathleen",
        "Nicholas", "Angela", "Eric", "Shirley", "Jonathan", "Anna", "Stephen", "Brenda",
        "Larry", "Pamela", "Justin", "Emma", "Scott", "Nicole", "Brandon", "Helen",
        "Benjamin", "Samantha", "Samuel", "Katherine", "Raymond", "Christine", "Patrick", "Debra",
        "Alexander", "Rachel", "Jack", "Carolyn", "Dennis", "Janet", "Jerry", "Maria"
    };

    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
        "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White",
        "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young",
        "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
        "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell",
        "Carter", "Roberts", "Gomez", "Phillips", "Evans", "Turner", "Diaz", "Parker",
        "Cruz", "Edwards", "Collins", "Reyes", "Stewart", "Morris", "Morales", "Murphy",
        "Cook", "Rogers", "Gutierrez", "Ortiz", "Morgan", "Cooper", "Peterson", "Bailey",
        "Reed", "Kelly", "Howard", "Ramos", "Kim", "Cox", "Ward", "Richardson"
    };

    private static final String[] DEPARTMENTS = {
        "Engineering", "Engineering", "Engineering", "Engineering", // 40% Engineering
        "Sales", "Sales", "Sales", // 30% Sales
        "Marketing", "Marketing", // 20% Marketing
        "Finance", "Human Resources", "Operations", "Customer Support", "Product Management", "Legal"
    };

    private static final String[] COUNTRIES = {
        "United States", "United States", "United States", "United States", // 45%
        "India", "India", "India", // 25%
        "United Kingdom", "United Kingdom", // 15%
        "Canada", "Germany", "Singapore", "Australia", "France", "Netherlands"
    };

    private static final String[] JOB_TITLES_ENG = {
        "Software Engineer", "Senior Software Engineer", "Staff Engineer", "Principal Engineer",
        "Engineering Manager", "Senior Engineering Manager", "Tech Lead", "Architect"
    };

    private static final String[] JOB_TITLES_SALES = {
        "Sales Representative", "Senior Sales Representative", "Account Executive",
        "Sales Manager", "Regional Sales Director", "VP of Sales"
    };

    private static final String[] JOB_TITLES_OTHER = {
        "Analyst", "Senior Analyst", "Manager", "Senior Manager", "Director", "Senior Director", "VP"
    };

    private static final String[] GENDERS = {"Male", "Female", "Non-Binary"};
    private static final String[] PERFORMANCE_RATINGS = {
        "Exceeds Expectations", "Meets Expectations", "Needs Improvement", "Outstanding"
    };
    private static final String[] CURRENCIES = {"USD", "INR", "GBP", "CAD", "EUR", "SGD", "AUD"};

    public DataSeeder(EmployeeRepository employeeRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        // Seed default users
        seedDefaultUsers();
        
        // Seed employees
        long count = employeeRepository.count();
        if (count < 10000) {
            LOG.info("Seeding database with employee data...");
            seedEmployees();
            LOG.info("Database seeding completed!");
        } else {
            LOG.info("Database already contains {} employees. Skipping seed.", count);
        }
    }

    private void seedDefaultUsers() {
        // Check and create HR user
        if (!userRepository.existsByUsername("hruser")) {
            User hr = new User();
            hr.setUsername("hruser");
            hr.setEmail("hr@acme.com");
            hr.setPassword(passwordEncoder.encode("hr123"));
            hr.setRole("HR");
            hr.setActive(true);
            userRepository.save(hr);
            LOG.info("Created HR user (hruser/hr123)");
        } else {
            LOG.info("HR user already exists. Skipping user seed.");
        }
    }

    private void seedEmployees() {
        List<Employee> employees = new ArrayList<>();
        
        for (int i = 1; i <= 10000; i++) {
            Employee employee = createRandomEmployee(i);
            employees.add(employee);

            // Batch insert every 1000 records
            if (i % 1000 == 0) {
                employeeRepository.saveAll(employees);
                employees.clear();
                LOG.info("Inserted {} employees...", i);
            }
        }

        // Insert remaining employees
        if (!employees.isEmpty()) {
            employeeRepository.saveAll(employees);
        }
    }

    private Employee createRandomEmployee(int index) {
        Employee employee = new Employee();
        
        String firstName = randomFrom(FIRST_NAMES);
        String lastName = randomFrom(LAST_NAMES);
        String department = randomFrom(DEPARTMENTS);
        String country = randomFrom(COUNTRIES);
        
        employee.setEmployeeCode(String.format("EMP%05d", index));
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(generateEmail(firstName, lastName, index));
        employee.setDepartment(department);
        employee.setCountry(country);
        employee.setJobTitle(getJobTitle(department));
        employee.setSalary(generateSalary(department, country));
        employee.setCurrency(getCurrency(country));
        employee.setDateJoined(generateDateJoined());
        employee.setGender(randomFrom(GENDERS));
        employee.setExperienceYears(random.nextInt(25));
        employee.setPerformanceRating(randomFrom(PERFORMANCE_RATINGS));
        employee.setIsActive(true);
        
        return employee;
    }

    private String generateEmail(String firstName, String lastName, int index) {
        return (firstName.toLowerCase() + "." + lastName.toLowerCase() + index + "@acme.com").replace(" ", "");
    }

    private String getJobTitle(String department) {
        return switch (department) {
            case "Engineering" -> randomFrom(JOB_TITLES_ENG);
            case "Sales" -> randomFrom(JOB_TITLES_SALES);
            default -> randomFrom(JOB_TITLES_OTHER);
        };
    }

    private BigDecimal generateSalary(String department, String country) {
        // Base salary with normal distribution
        double baseSalary = random.nextGaussian() * 25000 + 80000;
        baseSalary = Math.max(35000, Math.min(250000, baseSalary)); // Clamp between 35K-250K
        
        // Department multiplier
        double deptMultiplier = switch (department) {
            case "Engineering" -> 1.2;
            case "Sales" -> 1.0;
            case "Finance" -> 1.15;
            case "Product Management" -> 1.18;
            default -> 0.95;
        };
        
        // Country multiplier (cost of living adjustment)
        double countryMultiplier = switch (country) {
            case "United States" -> 1.3;
            case "United Kingdom", "Singapore" -> 1.15;
            case "Canada", "Australia" -> 1.1;
            case "Germany", "France", "Netherlands" -> 1.05;
            case "India" -> 0.4;
            default -> 1.0;
        };
        
        double finalSalary = baseSalary * deptMultiplier * countryMultiplier;
        return BigDecimal.valueOf(finalSalary).setScale(2, RoundingMode.HALF_UP);
    }

    private String getCurrency(String country) {
        return switch (country) {
            case "United States" -> "USD";
            case "India" -> "INR";
            case "United Kingdom" -> "GBP";
            case "Canada" -> "CAD";
            case "Germany", "France", "Netherlands" -> "EUR";
            case "Singapore" -> "SGD";
            case "Australia" -> "AUD";
            default -> "USD";
        };
    }

    private LocalDate generateDateJoined() {
        // Random date in last 10 years
        int daysAgo = random.nextInt(3650);
        return LocalDate.now().minusDays(daysAgo);
    }

    private <T> T randomFrom(T[] array) {
        return array[random.nextInt(array.length)];
    }
}
