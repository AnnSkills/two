package com.bsuir.annakhomyakova.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bsuir.annakhomyakova.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RulesAnnKhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RulesAnnKh.class);
        RulesAnnKh rulesAnnKh1 = new RulesAnnKh();
        rulesAnnKh1.setId(1L);
        RulesAnnKh rulesAnnKh2 = new RulesAnnKh();
        rulesAnnKh2.setId(rulesAnnKh1.getId());
        assertThat(rulesAnnKh1).isEqualTo(rulesAnnKh2);
        rulesAnnKh2.setId(2L);
        assertThat(rulesAnnKh1).isNotEqualTo(rulesAnnKh2);
        rulesAnnKh1.setId(null);
        assertThat(rulesAnnKh1).isNotEqualTo(rulesAnnKh2);
    }
}
