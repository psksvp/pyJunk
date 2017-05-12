

extern void __VERIFIER_error() __attribute__ ((__noreturn__));

void __VERIFIER_assert(int cond) {
  if (!(cond)) {
    ERROR: __VERIFIER_error();
  }
  return;
} 


int main(int argc, char** arg)
{ 
  int i = 0;
  do
  {
    i = i + 1;
  }while(i < 10);
  __VERIFIER_assert(i == 10); 
  return 0; 
} 
