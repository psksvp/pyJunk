extern void __VERIFIER_error() __attribute__ ((__noreturn__));

void __VERIFIER_assert(int cond) {
  if (!(cond)) {
    ERROR: __VERIFIER_error();
  }
  return;
}

unsigned int __VERIFIER_nondet_uint();

int main()
{
  int x = __VERIFIER_nondet_uint();
  x++;
  do
  {
    x--;
  }while(x > 0);
    /*
  while(x > 0)
  {
    x--;
  } */
  __VERIFIER_assert(x == 0);
  return 0;
}