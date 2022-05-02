package com.bsuir.annakhomyakova.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bsuir.annakhomyakova.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BagAnnKhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BagAnnKh.class);
        BagAnnKh bagAnnKh1 = new BagAnnKh();
        bagAnnKh1.setId(1L);
        BagAnnKh bagAnnKh2 = new BagAnnKh();
        bagAnnKh2.setId(bagAnnKh1.getId());
        assertThat(bagAnnKh1).isEqualTo(bagAnnKh2);
        bagAnnKh2.setId(2L);
        assertThat(bagAnnKh1).isNotEqualTo(bagAnnKh2);
        bagAnnKh1.setId(null);
        assertThat(bagAnnKh1).isNotEqualTo(bagAnnKh2);
    }
}
