package com.example.company;

import com.example.company.base.AbstractTest;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ExtendWith({RandomBeansExtension.class, MockitoExtension.class})
public abstract class AbstractUnitTest extends AbstractTest {}
