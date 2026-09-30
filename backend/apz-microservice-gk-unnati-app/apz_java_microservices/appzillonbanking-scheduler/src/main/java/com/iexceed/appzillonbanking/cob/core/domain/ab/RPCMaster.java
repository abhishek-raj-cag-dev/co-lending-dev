package com.iexceed.appzillonbanking.cob.core.domain.ab;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_abob_rpc_master")
@Getter
@Setter
@ToString
public class RPCMaster {

    @EmbeddedId
    private TbAbobRpcMasterId id;

    @Column(name = "stage_name", length = 100)
    private String stageName;

    @Column(name = "stage_dtls", length = 22000)
    private String stageDtls;

    @Column(name = "queries", length = 22000)
    private String queries;

    // ======================
    // STATIC INNER ID CLASS
    // ======================
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TbAbobRpcMasterId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "id", nullable = false)
        private BigDecimal id;

        @Column(name = "app_id", nullable = false, length = 6)
        private String appId;
    }
}
