﻿using System;
using System.Collections.Generic;
using System.Linq;
using NUnit.Framework;
using AutofacContrib.NSubstitute;

using Bowling;
using NSubstitute;

[TestFixture]
public class GameTest
{
    protected IGame game { get; private set; }

    [SetUp]
    public void SetUp()
    {
        game = Substitute.For<IGame>();
    }
    
    [Test]

    public void RollSomePins_GetSomeScore()
    {
        // Arrange
        game = new Game();
        game.Roll(8);
        game.Roll(1);

        // Act
        var result = game.Score;

        // Assert
        Assert.AreEqual(9, result);
    }

    [Test]
    [Category("gutter")]
    public void RollNonePins_GetNoneScore()
    {
        // Arrange
        game = new Game();
        game.Roll(0);
        game.Roll(1);

        // Act
        var result = game.Score;

        // Assert
        Assert.Greater(result, 0);
        Assert.Less(result, 10);
    }

    [Test]
    [Category("gutter")]
    public void RollAnotherPins_GetNoneScore()
    {
        // Arrange
        game = new Game();
        game.Roll(0);
        game.Roll(3);

        // Act
        var result = game.Score;

        // Assert
        Assert.Greater(result, 0);
        Assert.Less(result, 10);
    }
}
