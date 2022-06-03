package com.hse.knopkabackend.services;

import com.hse.knopkabackend.DTO.BatchDTO;
import com.hse.knopkabackend.configs.ClickHouseKnopkaConfig;
import com.hse.knopkabackend.models.knopkauser.KnopkaUser;
import com.hse.knopkabackend.repositories.entityforclick.EntityForClickRepository;
import com.hse.knopkabackend.repositories.knopkauser.KnopkaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

@Service
public class EntityForClickService {
    private final EntityForClickRepository entityForClickRepository;
    private final KnopkaUserRepository knopkaUserRepository;

    @Autowired
    public EntityForClickService(EntityForClickRepository entityForClickRepository, KnopkaUserRepository knopkaUserRepository) {
        this.entityForClickRepository = entityForClickRepository;
        this.knopkaUserRepository = knopkaUserRepository;
    }


    public int getClicks(Long knopkaId) {
        return entityForClickRepository.getNumberOfClicks(knopkaId).size();
    }

    public void registerNewBatch(Long knopkaUserId, String token, BatchDTO batchDTO) {
        KnopkaUser knopkaUserById = knopkaUserRepository.findById(knopkaUserId).orElseThrow(() -> {
            throw new IllegalStateException("Invalid id");
        });
        if (Objects.equals(token, knopkaUserById.getToken())) {
            StringBuilder queryBuffer = new StringBuilder("INSERT INTO entityforclick (click_id, clicked_knopka_id, region, time_of_click) FORMAT Values ");
            Long pushes = batchDTO.getPushes();
            while (pushes > 0) {
                StringBuilder queryPart = new StringBuilder("(" + "1, '" + batchDTO.getClickedKnopkaId() + "', '" + batchDTO.getRegion() + "', '" + batchDTO.getTime() + "')");
                pushes--;
                if (pushes != 0) queryPart.append(", ");
                queryBuffer.append(queryPart);
            }
            System.out.println(queryBuffer);
            try {
                Connection conn = ClickHouseKnopkaConfig.getConnection();
                Statement statement = conn.createStatement();
                statement.executeQuery(queryBuffer.toString());
                conn.close();
            } catch (SQLException e) {
                throw new IllegalStateException("ClickHouse connection problems", e);
            }

            System.out.println("Registered batch with time: " + batchDTO.getTime());
        } else {
            throw new IllegalStateException("Token is invalid");
        }
    }
}
