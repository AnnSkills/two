package com.bsuir.annakhomyakova.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bsuir.annakhomyakova.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupportAnnKhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupportAnnKh.class);
        SupportAnnKh supportAnnKh1 = new SupportAnnKh();
        supportAnnKh1.setId(1L);
        SupportAnnKh supportAnnKh2 = new SupportAnnKh();
        supportAnnKh2.setId(supportAnnKh1.getId());
        assertThat(supportAnnKh1).isEqualTo(supportAnnKh2);
        supportAnnKh2.setId(2L);
        assertThat(supportAnnKh1).isNotEqualTo(supportAnnKh2);
        supportAnnKh1.setId(null);
        assertThat(supportAnnKh1).isNotEqualTo(supportAnnKh2);
    }
}
