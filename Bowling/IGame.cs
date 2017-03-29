namespace Bowling
{
    public interface IGame
    {
        int Score { get; }

        void Roll(int pins);
    }
}