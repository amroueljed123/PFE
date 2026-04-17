package com.esb.appback.service;

import com.esb.appback.dto.ParsedArticleDTO;
import com.esb.appback.model.*;
import java.util.Map;
import java.util.UUID;

public class ArticleFactory {

    public static Article createFromOcr(ParsedArticleDTO dto) {
        String type = dto.getType().toUpperCase();
        Article a = null;
        Map<String, String> c = dto.getCharacteristics();

        switch(type) {
            case "ORDINATEUR":
                Ordinateur ord = new Ordinateur();
                ord.setMarque(c.getOrDefault("marque", "Inconnu"));
                ord.setProcesseur(c.getOrDefault("processeur", "Inconnu"));
                ord.setRam(c.getOrDefault("ram", "8Go"));
                a = ord;
                break;
            case "MONITEUR":
                Moniteur mon = new Moniteur();
                mon.setMarque(c.getOrDefault("marque", "Inconnu"));
                mon.setResolution(c.getOrDefault("resolution", "1080p"));
                mon.setTaille(c.getOrDefault("taille", "24"));
                a = mon;
                break;
            case "TELEPHONE":
                Telephone tel = new Telephone();
                tel.setMarque(c.getOrDefault("marque", "Inconnu"));
                tel.setSystemeExploitation(c.getOrDefault("os", "Android"));
                a = tel;
                break;
            case "USB":
            case "PERIPHERIQUE":
                Peripherique p = new Peripherique();
                p.setMarque(c.getOrDefault("marque", "Inconnu"));
                p.setTypePeripherique(c.getOrDefault("type", "Clavier"));
                p.setInterfaceConnexion(c.getOrDefault("connectivite", "USB"));
                a = p;
                break;
            case "IMPRIMANTE":
                ModeleImprimante imp = new ModeleImprimante();
                imp.setMarque(c.getOrDefault("marque", "Inconnu"));
                imp.setTypeImprimante(c.getOrDefault("type", "Laser"));
                a = imp;
                break;
            case "CARTOUCHE":
                Cartouche car = new Cartouche();
                car.setReference(c.getOrDefault("marque", "Inconnu"));
                car.setCouleur(c.getOrDefault("couleur", "Noir"));
                a = car;
                break;
            case "BUREAU":
                Bureau b = new Bureau();
                b.setTypeDeSurface(c.getOrDefault("materiau", "Bois"));
                a = b;
                break;
            case "CHAISE":
                Chaise ch = new Chaise();
                ch.setMateriau(c.getOrDefault("materiau", "Tissu"));
                ch.setTypeDeDossier(c.getOrDefault("typededossier", "Standard"));
                a = ch;
                break;
            case "MEUBLE":
                Meuble m = new Meuble();
                m.setMateriau(c.getOrDefault("materiau", "Acier"));
                m.setDimension(c.getOrDefault("dimensions", "80x40x120"));
                a = m;
                break;
            case "ETAGERE":
                EtagereIndustrielle e = new EtagereIndustrielle();
                a = e;
                break;
            default:
                a = new Article();
                break;
        }

        // Generic Mapping for all items
        a.setType(type.equals("USB") ? "PERIPHERIQUE" : type);
        a.setNom(dto.getDescription());
        a.setDescription("Importé auto par OCR: " + c.toString());
        a.setQuantite(dto.getQuantity() != null ? dto.getQuantity() : 1);
        a.setPrix(dto.getUnitPrice() != null ? dto.getUnitPrice() : 0.0);
        a.setUnite("U");
        a.setFamille(type.matches("BUREAU|CHAISE|MEUBLE|ETAGERE") ? "MOBILIER" : "INFORMATIQUE");
        a.setFournisseur("Auto");
        a.setCodeBarre("OCR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        a.setStatut("En stock");

        return a;
    }
}
