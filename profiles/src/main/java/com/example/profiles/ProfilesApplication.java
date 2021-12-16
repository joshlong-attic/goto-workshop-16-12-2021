package com.example.profiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class ProfilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfilesApplication.class, args);
    }

}

@Controller
@ResponseBody
class ProfileRestController {

    private final Map<Integer, Profile> db = new ConcurrentHashMap<>();

    ProfileRestController() {
        for (var customerId = 1; customerId <= 2; customerId++)
            this.db.put(customerId, new Profile(customerId, customerId));
    }

    @GetMapping("/profiles/{ids}")
    Collection<Profile> getForIds(@PathVariable Collection<Integer> ids) {
        var results = new ArrayList<Profile>();
        ids.forEach(id -> {
            if (this.db.containsKey(id))
                results.add(this.db.get(id));

        });
        return results;
    }

    @GetMapping("/profile/{id}")
    Profile getById(@PathVariable Integer id) {
        return this.db.get(id);
    }
}

record Profile(Integer id, Integer customerId) {
}