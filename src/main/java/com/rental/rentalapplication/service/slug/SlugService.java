package com.rental.rentalapplication.service.slug;

import java.text.Normalizer;

public class SlugService {
    public static String toSlug(String input) {
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return slug.toLowerCase().replaceAll("[^-a-z0-9]", "").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
