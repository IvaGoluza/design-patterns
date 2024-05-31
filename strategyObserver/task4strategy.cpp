#include <iostream>
#include <vector>
#include <algorithm>
#include <cmath>
#include <random>
#include <cassert>

using namespace std;

// [strategy interface] abstract class for number generation 
class NumberGenerator {
public:
    virtual vector<int> generateNumbers() = 0;  // pure method!
    virtual ~NumberGenerator() {}
};


// [concrete strategy] for sequential number generation
class SequentialGenerator : public NumberGenerator {
private:
    int lowerlimit_;
    int upperlimit_;
    int step_;
public:
    SequentialGenerator(int lowerlimit, int upperlimit, int step) : lowerlimit_(lowerlimit), upperlimit_(upperlimit), step_(step) {}

    vector<int> generateNumbers() override {
        vector<int> numbers;
        int i = lowerlimit_;
        while(i <= upperlimit_) {
            numbers.push_back(i);
            i+=step_;
        }
        return numbers;
    }
};


// [concrete strategy] for random number generation
class RandomGenerator : public NumberGenerator {
private:
    double mean_;
    double stddev_;
    int num_;
public:
    RandomGenerator(double mean, double stddev, int num) : mean_(mean), stddev_(stddev), num_(num) {}

    vector<int> generateNumbers() override {
        default_random_engine generator;
        normal_distribution<double> distribution(mean_, stddev_);
        vector<int> numbers;

        for (int i = 0; i < num_; i++) {
            numbers.push_back(static_cast<int>(round(distribution(generator))));
        }
        return numbers;
    }
};


// [concrete strategy] for number generation using fibonacci sequence
class FibonacciGenerator : public NumberGenerator {
private:
    int n_;
public:
    FibonacciGenerator(int n) : n_(n) {}

    vector<int> generateNumbers() override {
        vector<int> numbers;
        int f0 = 0, f1 = 1;
        for (int i=0; i < n_; i++) {
            numbers.push_back(f0);
            int pom = f0 + f1;
            f0 = f1;
            f1 = pom;
        }
        return numbers;
    }
};


// [strategy interface] abstract class for percentiles
class PercentileCalculator {
public:
    virtual int calculatePercentile(vector<int>& numbers, int percentile) = 0;
    virtual ~PercentileCalculator() {}
};


// [concrete strategy] nearest-rank method 
class PositionPercentileCalculator : public PercentileCalculator {
public:
    int calculatePercentile(vector<int>& numbers, int percentile) override {
        assert(percentile >= 0 && percentile <= 100);
        sort(numbers.begin(), numbers.end());
        int n_p = static_cast<int>(round(percentile * numbers.size() / 100.0 + 0.5));
        return numbers[n_p-1];
    }
};

// [concrete strategy] linear interpolation between closest ranks method 
class InterpolationPercentileCalculator : public PercentileCalculator {
public:
    int calculatePercentile(std::vector<int>& numbers, int percentile) override {
        sort(numbers.begin(), numbers.end());
         
        for (int i = 0; i < numbers.size(); i++) {
            double pv_i = 100 * ((i + 1 - 0.5) / numbers.size());
            double pv_iplus1 = 100 * ((i + 2 - 0.5) / numbers.size());

            if((i == 0 && percentile <= pv_i) || (i == numbers.size()-1 && percentile >= pv_i)) return numbers[i];

            if (pv_i < percentile && percentile < pv_iplus1) {
                int interpolatedValue = static_cast<int>(numbers[i] + numbers.size() * (percentile - pv_i) * ((numbers[i + 1] - numbers[i]) / 100.0));
                return interpolatedValue;
            }
        }

        return INT_MIN; 
    }
};

// [context]
class DistributionTester {
private:
    NumberGenerator* numberGenerator_;
    PercentileCalculator* percentileCalculator_;
public:
    DistributionTester(NumberGenerator* numberGenerator, PercentileCalculator* percentileCalculator) 
        : numberGenerator_(numberGenerator), percentileCalculator_(percentileCalculator) {}

    vector<int> generateNumbers() {
        return numberGenerator_->generateNumbers();
    }

    int calculatePercentile(vector<int>& numbers, double percentile) {
        return percentileCalculator_->calculatePercentile(numbers, percentile);
    }
};

int main() {

    SequentialGenerator sequentialGenerator(2, 23, 2);
    RandomGenerator randomGenerator(1.5, 2.2, 10);
    FibonacciGenerator fibonacciGenerator(10);

    PositionPercentileCalculator positionPercentileCalculator;
    InterpolationPercentileCalculator interpolationCalculator;

    DistributionTester distTester1(&fibonacciGenerator, &positionPercentileCalculator);
    vector<int> numbers = distTester1.generateNumbers();

    cout << "Numbers" << endl;
    for (int num : numbers) {
        cout << num << " ";
    }
    cout << endl;

    int percentile = 80;
    int result = distTester1.calculatePercentile(numbers, percentile);
    cout << "Percentile " << percentile << ": " << result << endl;

    return 0;
}
