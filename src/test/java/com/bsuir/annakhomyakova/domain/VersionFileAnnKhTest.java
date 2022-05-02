package com.bsuir.annakhomyakova.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bsuir.annakhomyakova.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VersionFileAnnKhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VersionFileAnnKh.class);
        VersionFileAnnKh versionFileAnnKh1 = new VersionFileAnnKh();
        versionFileAnnKh1.setId(1L);
        VersionFileAnnKh versionFileAnnKh2 = new VersionFileAnnKh();
        versionFileAnnKh2.setId(versionFileAnnKh1.getId());
        assertThat(versionFileAnnKh1).isEqualTo(versionFileAnnKh2);
        versionFileAnnKh2.setId(2L);
        assertThat(versionFileAnnKh1).isNotEqualTo(versionFileAnnKh2);
        versionFileAnnKh1.setId(null);
        assertThat(versionFileAnnKh1).isNotEqualTo(versionFileAnnKh2);
    }
}
